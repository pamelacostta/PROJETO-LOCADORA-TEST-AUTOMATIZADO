package builders;

import entidades.Filme;
import entidades.Locacao;
import entidades.Usuario;

import java.util.Arrays;
import java.util.Date;

import static utils.DataUtils.obterDataComDiferencaDias;

public class LocacaoBuilder {
    private Locacao elemento;

    private LocacaoBuilder() {
    }

    public static LocacaoBuilder umLocacao() {
        LocacaoBuilder builder = new LocacaoBuilder();
        inicializarDadosPadroes(builder);
        return builder;
    }

    public static void inicializarDadosPadroes(LocacaoBuilder builder) {
        builder.elemento = new Locacao();
        Locacao elemento = builder.elemento;


        elemento.setUsuario(UsuarioBuilder.umUsuario().agora());
        elemento.setFilmes(Arrays.asList(FilmeBuilder.umFilme().agora()));
        elemento.setDataLocacao(new Date());
        elemento.setDataRetorno(obterDataComDiferencaDias(1));
        elemento.setValor(4.0);
    }

    public LocacaoBuilder comUsuario(Usuario param) {
        elemento.setUsuario(param);
        return this;
    }

    public LocacaoBuilder comListaFilmes(Filme... params) {
        elemento.setFilmes(Arrays.asList(params));
        return this;
    }

    public LocacaoBuilder comDataLocacao(Date param) {
        elemento.setDataLocacao(param);
        return this;
    }

    public LocacaoBuilder comDataRetorno(Date param) {
        elemento.setDataRetorno(param);
        return this;
    }

    public LocacaoBuilder comValor(Double param) {
        elemento.setValor(param);
        return this;
    }

    public Locacao agora() {
        return elemento;
    }

    public LocacaoBuilder atrasada() {

        elemento.setDataLocacao(obterDataComDiferencaDias(-4));
        elemento.setDataRetorno(obterDataComDiferencaDias(-2));

        return this;
    }
}