package pe.bienesraices.dao;

import java.util.List;

import pe.bienesraices.modelo.Pedido;

public interface PedidoDAO {
    int insertar(Pedido pedido);

    List<Pedido> listar();
}
