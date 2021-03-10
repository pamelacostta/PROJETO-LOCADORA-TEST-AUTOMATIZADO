package service;

import entidades.Usuario;

public interface SPCService {

    boolean possuiNegativacao(Usuario usuario) throws Exception;
}