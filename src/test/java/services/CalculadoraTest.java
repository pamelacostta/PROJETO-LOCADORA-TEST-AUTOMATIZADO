package services;

import exceptions.NaoPodeDividirPorZeroException;
import org.junit.*;
import service.Calculadora;


public class CalculadoraTest {

    public static StringBuffer ordem = new StringBuffer();
    private Calculadora calc;

    @Before
    public void setup() {

        calc = new Calculadora();
        System.out.println("iniciando...");
        ordem.append("1");
    }

    @After
    public void tearDown() {
        System.out.println("finalizando...");
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println(ordem.toString());
    }

    @Test
    public void deveSomarDoisValores() {

        // Cenário
        int a = 5;
        int b = 3;

        // Ação
        int resultado = calc.somar(a, b);

        // Verificação
        Assert.assertEquals(8, resultado);
    }

    @Test
    public void deveSubtrairDoisValores() {

        // Cenário
        int a = 8;
        int b = 5;

        // Ação
        int resultado = calc.subtrair(a, b);

        // Verificação
        Assert.assertEquals(3, resultado);
    }

    @Test
    public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {

        // Cenário
        int a = 6;
        int b = 3;

        // Ação
        int resultado = calc.dividir(a, b);

        // Verificação
        Assert.assertEquals(2, resultado);
    }

    @Test(expected = NaoPodeDividirPorZeroException.class)
    public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {

        // Cenário
        int a = 10;
        int b = 0;

        // Ação
        calc.dividir(a, b);
    }
}