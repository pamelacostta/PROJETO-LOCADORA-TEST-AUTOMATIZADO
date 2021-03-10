package services;

import daos.LocacaoDAO;
import entidades.Filme;
import entidades.Locacao;
import entidades.Usuario;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import service.EmailService;
import service.LocacaoService;
import service.SPCService;
import utils.DataUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static builders.FilmeBuilder.umFilme;
import static builders.UsuarioBuilder.umUsuario;
import static matchers.MatchersProprios.caiNumaSegunda;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LocacaoService.class })
public class LocacaoServiceTest_PowerMock {

    @Rule
    public final ErrorCollector error = new ErrorCollector();
    @InjectMocks
    private LocacaoService service;
    @Mock
    private LocacaoDAO dao;
    @Mock
    private SPCService spc;
    @Mock
    private EmailService email;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        service = PowerMockito.spy(service);
        System.out.println("Iniciando 4...");
        CalculadoraTest.ordem.append("4");
    }

    @After
    public void tearDown() {
        System.out.println("finalizando 4...");
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println(CalculadoraTest.ordem.toString());
    }

    @Test
    public void deveAlugarFilme() throws Exception {

        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = new ArrayList<>();
        filmes.add(umFilme().comValor(5.0).agora());
        PowerMockito.whenNew(Date.class).withNoArguments()
                .thenReturn(DataUtils.obterData(28, 4, 2017));

        // ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(),
                DataUtils.obterData(28, 4, 2017)), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(),
                DataUtils.obterData(29, 4, 2017)), is(true));
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());
        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017));

        // Ação
        Locacao retorno = service.alugarFilme(usuario, filmes);

        // Verificação
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
        PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
    }

    @Test
    public void deveAlugarFilme_SemCalcularValor() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());
        PowerMockito.doReturn(1.0)
                .when(service, "calcularValorLocacao", filmes);

        // Ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // Verificação
        assertThat(locacao.getValor(), is(1.0));
        PowerMockito.verifyPrivate(service)
                .invoke("calcularValorLocacao", filmes);
    }

    @Test
    public void deveCalcularValorLocaocao() throws Exception {

        // Cenário
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        // Ação
        Double valor = Whitebox.invokeMethod(service,
                "calcularValorLocacao", filmes);
        // Verificação
        assertThat(valor, is(4.0));
    }
}
