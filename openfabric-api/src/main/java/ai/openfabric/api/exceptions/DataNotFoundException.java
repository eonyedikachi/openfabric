package ai.openfabric.api.exceptions;

public class DataNotFoundException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private String errorMessage;

        public DataNotFoundException(String errorMessage) {
            super( errorMessage);
        }

}
