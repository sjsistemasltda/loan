package br.company.loan;

import javax.print.attribute.standard.MediaSize;

public interface Constants {
    String PATH_SEPARATOR = "/";
    interface CONTROLLER {
        String VERSION = "v1";

        interface PERSON {
            String NAME = "persons";
            String PATH = PATH_SEPARATOR + VERSION + PATH_SEPARATOR + NAME;
            String SLUG = "id";
            String PATH_SLUG = PATH_SEPARATOR + "{" + SLUG + "}";

            interface UPDATE {
                String PATH = PERSON.PATH + PERSON.PATH_SLUG;
            }

            interface DELETE {
                String PATH = PERSON.PATH + PERSON.PATH_SLUG;
            }

            interface GET {
                String PATH = PERSON.PATH + PERSON.PATH_SLUG;
            }

            interface LOAN {
                String NAME = "loans";
                String PATH = PERSON.PATH + PATH_SEPARATOR + PERSON.PATH_SLUG + PATH_SEPARATOR + NAME;
            }
        }
    }

    interface RDS {
        String SCHEMA = "bank";
        String DOT = ".";

        interface TABLE {
            interface LOAN {
                String NAME = "loan";
                String SEQ = SCHEMA + DOT + NAME + "_seq";
            }

            interface PERSON {
                String NAME = "person";
                String SEQ = SCHEMA + DOT + NAME + "_seq";
            }
        }
    }
}
