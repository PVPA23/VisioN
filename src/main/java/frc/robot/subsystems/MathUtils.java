package frc.robot.subsystems;

public class MathUtils {

    /**
     * Deadband inteligente.
     */
    public static double smartDeadband(double value, double deadband) {
        if (Math.abs(value) < deadband)
            return 0;

        double scaled = (Math.abs(value) - deadband) / (1 - deadband);
        return Math.copySign(scaled, value);
    }

    /*
     * Curva exponencial suave para melhorar a direção fina.
     * expo: 0.0 = sem curva | 1.0 = muito suave
     */
    public static double expoCurve(double value, double expo) {
        return Math.copySign(Math.pow(Math.abs(value), 1 + expo), value);
    }
}
