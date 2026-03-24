public final class ProblemEmailAlreadyInUse extends Problem {

    public static final ProblemEmailAlreadyInUse INSTANCE;

    static {
        INSTANCE = new ProblemEmailAlreadyInUse();
    }

    private ProblemEmailAlreadyInUse() {
        super("Email Already In Use", "There is already a user with given email");
    }

    public static void main(String[] args) {
        System.out.println(ProblemEmailAlreadyInUse.INSTANCE);
    }
}