package services;
import daos.LocacaoDAO;
import entidades.Filme;
import entidades.Locacao;
import entidades.Usuario;
import exceptions.FilmeSemEstoqueException;
import exceptions.LocadoraException;
import matchers.MatchersProprios;
import service.EmailService;
import service.LocacaoService;
import service.SPCService;
import utils.DataUtils;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.mockito.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static builders.FilmeBuilder.umFilme;
import static builders.LocacaoBuilder.umLocacao;
import static builders.UsuarioBuilder.umUsuario;
import static matchers.MatchersProprios.ehHoje;
import static matchers.MatchersProprios.ehHojeComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class LocacaoServiceTest {

    @Rule
    public final ErrorCollector error = new ErrorCollector();
    @InjectMocks
    @Spy
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
        System.out.println("Iniciando 2...");
        CalculadoraTest.ordem.append("2");
    }

    @After
    public void tearDown() {
        System.out.println("finalizando 2...");
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
        Mockito.doReturn(DataUtils.obterData(28, 4, 2017))
                .when(service).obterData();
        // ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(),
                DataUtils.obterData(28, 4, 2017)), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(),
                DataUtils.obterData(29, 4, 2017)), is(true));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void naoDeveAlugarFilmeSemEstoque() throws Exception {
        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = new ArrayList<>();
        filmes.add(umFilme().semEstoque().agora());

        // ação
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        // cenario
        List<Filme> filmes = new ArrayList<>();

        filmes.add(umFilme().agora());
        filmes.add(umFilme().agora());
        filmes.add(umFilme().agora());

        // ação
        try {
            service.alugarFilme(null, filmes);
            fail("Esperado que não houvesse usuário");
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuário vazio"));
        }
        System.out.println("Forma robusta");
    }

    @Test
    public void naoDeveAlugarFilmeSemFilme() {
        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = new ArrayList<>();

        // ação e Verificação
        assertThrows("Não deveria haver filme ou outra exceção ocorreu",
                LocadoraException.class, () -> service.alugarFilme(usuario, filmes)
        );
        System.out.println("Forma nova");
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());
        Mockito.doReturn(DataUtils.obterData(29, 4, 2017))
                .when(service).obterData();
        // Ação
        Locacao retorno = service.alugarFilme(usuario, filmes);

        // Verificação
        assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

        // Ação e verificação
        Exception exception = assertThrows(LocadoraException.class,
                () -> service.alugarFilme(usuario, filmes));

        assertThat(exception.getMessage(), is("Usuário negativado"));

        verify(spc).possuiNegativacao(usuario);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas() {

        // Cenário
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
        Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
        List<Locacao> locacoes = Arrays.asList(
                umLocacao().comUsuario(usuario).atrasada().agora(),
                umLocacao().comUsuario(usuario2).agora(),
                umLocacao().comUsuario(usuario3).atrasada().agora(),
                umLocacao().comUsuario(usuario3).atrasada().agora());

        when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        // Ação
        service.notificarAtrasos();

        // Verificação
        verify(email, times(3)).notificarAtraso(any(Usuario.class));
        verify(email).notificarAtraso(usuario);
        verify(email, atLeastOnce()).notificarAtraso(usuario3);
        verify(email, never()).notificarAtraso(usuario2);
        verifyNoMoreInteractions(email);
    }

    @Test
    public void deveTratarErronoSPC() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica"));

        // Ação e verificação
        Exception exception = assertThrows(LocadoraException.class,
                () -> service.alugarFilme(usuario, filmes));

        assertThat(exception.getMessage(), is("Problemas com SPC, tente novamente"));
    }

    @Test
    public void deveProrrogarUmaLocacao() {

        // Cenário
        Locacao locacao = umLocacao().agora();

        // Ação
        service.prorrogarLocacao(locacao, 3);

        // Verificação
        ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argCapt.capture());
        Locacao locacaoRetornada = argCapt.getValue();

        error.checkThat(locacaoRetornada.getValor(), is(12.0));
        error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
        error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
    }

    @Test
    public void deveCalcularValorLocaocao() throws Exception {

        // Cenário
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        // Ação
        Class<LocacaoService> clazz = LocacaoService.class;
        Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
        metodo.setAccessible(true);
        Double valor = (Double) metodo.invoke(service, filmes);

        // Verificação
        assertThat(valor, is(4.0));
    }
}