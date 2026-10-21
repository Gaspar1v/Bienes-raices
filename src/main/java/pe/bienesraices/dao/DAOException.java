package pe.bienesraices.dao;

public class DAOException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public DAOException(String mensaje) {
        super(mensaje);
    }
}
