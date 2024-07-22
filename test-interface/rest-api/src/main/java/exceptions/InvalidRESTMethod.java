package exceptions;

public class InvalidRESTMethod extends Exception {

    private static final long serialVersionUID = 7864495898282997657L;

    public InvalidRESTMethod(String methodName) {
        super("Rest API Method '" + methodName + "' is either invalid or not supported. \n" +
                "Currently supporting methods are - GEt, POST, PUT, PATCH, DELETE, OPTIONS.\n" +
                "To add support, Please contact automation team");
    }
}
