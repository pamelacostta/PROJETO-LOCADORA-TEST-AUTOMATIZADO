package services;


import daos.LocacaoDAO;
import entidades.Filme;
import entidades.Locacao;
import entidades.Usuario;
import exceptions.FilmeSemEstoqueException;
import exceptions.LocadoraException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import service.LocacaoService;
import service.SPCService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static builders.FilmeBuilder.umFilme;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    private static final Filme filme1 = umFilme().agora();
    private static final Filme filme2 = umFilme().agora();
    private static final Filme filme3 = umFilme().agora();
    private static final Filme filme4 = umFilme().agora();
    private static final Filme filme5 = umFilme().agora();
    private static final Filme filme6 = umFilme().agora();
    private static final Filme filme7 = umFilme().agora();

    @Parameterized.Parameter
    public List<Filme> filmes;
    @Parameterized.Parameter(value = 1)
    public Double valorLocacao;
    @Parameterized.Parameter(value = 2)
    public String cenario;
    @InjectMocks
    private LocacaoService service;
    @Mock
    private LocacaoDAO dao;
    @Mock
    private SPCService spc;

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getParametros() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList(filme1, filme2), 8.0, "2 filmes: Sem desconto"},
                {Arrays.asList(filme1, filme2, filme3), 11.0, "3 filmes: 25%"},
                {Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 filmes: 50%"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5),
                        14.0, "5 filmes: 75%"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6),
                        14.0, "6 filmes: 100%"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7),
                        18.0, "7 filmes: Sem desconto"},
        });
    }

    @Before
    public void setup() {

        initMocks(this);
        System.out.println("Iniciando 3...");
        CalculadoraTest.ordem.append("3");

    }

    @After
    public void tearDown() {
        System.out.println("finalizando 3...");
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println(CalculadoraTest.ordem.toString());
    }

    @Test
    public void deveCalcularValorLocacaoConsiderandoDescontos()
            throws FilmeSemEstoqueException, LocadoraException, InterruptedException {

        // Cenário
        Usuario usuario = new Usuario("Usuario 1");
        Thread.sleep(5000);

        // Açao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        // Verificação
        assertThat(resultado.getValor(), is(valorLocacao));
    }
}
