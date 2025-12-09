package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DriveSubsystem extends SubsystemBase {

    // Motores
    private SparkMax leftLeader;
    private SparkMax leftFollower;
    private SparkMax rightLeader;
    private SparkMax rightFollower;

    // Shuffleboard
    private ShuffleboardTab driveTab = Shuffleboard.getTab("Drive Metrics");

    private double lastLeftSpeed = 0;
    private double lastRightSpeed = 0;
    private double lastDeadband = 0;

    private double estimatedYaw = 0; // Integração simples da rotação
    private double estimatedSpeed = 0; // Velocidade aproximada (m/s)

    public DriveSubsystem() {

        // ------- INSTANCIAR MOTORES -------
        leftLeader    = new SparkMax(1, MotorType.kBrushed);
        leftFollower  = new SparkMax(2, MotorType.kBrushed);
        rightLeader   = new SparkMax(3, MotorType.kBrushed);
        rightFollower = new SparkMax(4, MotorType.kBrushed);

        // ------- CONFIG BASE -------
        SparkMaxConfig base = new SparkMaxConfig();

        base
            .smartCurrentLimit(40)
            .idleMode(IdleMode.kBrake)
            .openLoopRampRate(0.3);

        // ------- CONFIG INDIVIDUAL -------
        SparkMaxConfig leftLeaderCfg = new SparkMaxConfig();
        leftLeaderCfg.apply(base);

        SparkMaxConfig leftFollowerCfg = new SparkMaxConfig();
        leftFollowerCfg.apply(base).follow(leftLeader);

        SparkMaxConfig rightLeaderCfg = new SparkMaxConfig();
        rightLeaderCfg.apply(base).inverted(true);

        SparkMaxConfig rightFollowerCfg = new SparkMaxConfig();
        rightFollowerCfg.apply(base).follow(rightLeader);

        // ------- APLICAR CONFIGS -------
        leftLeader   .configure(leftLeaderCfg,    ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
        leftFollower .configure(leftFollowerCfg,  ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
        rightLeader  .configure(rightLeaderCfg,   ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
        rightFollower.configure(rightFollowerCfg, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);

        // ===========================================================
        // SHUFFLEBOARD MÉTRICAS
        // ===========================================================
        driveTab.addDouble("Left Speed",  () -> lastLeftSpeed);
        driveTab.addDouble("Right Speed", () -> lastRightSpeed);

        driveTab.addDouble("Left Voltage",  () -> leftLeader.getBusVoltage() * lastLeftSpeed);
        driveTab.addDouble("Right Voltage", () -> rightLeader.getBusVoltage() * lastRightSpeed);

        driveTab.addDouble("Deadband", () -> lastDeadband);

        driveTab.addDouble("Estimated Speed (m/s)", () -> estimatedSpeed);

        driveTab.addDouble("Estimated Orientation (Yaw°)", () -> estimatedYaw);

        driveTab.addBoolean("Motors Enabled", () ->
            Math.abs(lastLeftSpeed) > 0.01 || Math.abs(lastRightSpeed) > 0.01
        );
    }

    // ===============================================================
    // =============         ARCADE DRIVE             ================
    // ===============================================================
    public void arcadeDrive(double forward, double rotation, boolean boost) {

        // DEADZONE
        double deadband = 0.07;
        lastDeadband = deadband;

        if (Math.abs(forward) < deadband) forward = 0;
        if (Math.abs(rotation) < deadband) rotation = 0;

        // BOOST
        double boostFactor = boost ? 1.2 : 0.8;
        forward  *= boostFactor;
        rotation *= boostFactor;

        // CÁLCULO
        double leftSpeed  = forward + rotation;
        double rightSpeed = forward - rotation;

        // LIMITAÇÃO
        leftSpeed  = Math.max(Math.min(leftSpeed, .5), -.5);
        rightSpeed = Math.max(Math.min(rightSpeed, .5), -.5);

        // ENVIAR PARA MOTORES
        leftLeader.set(leftSpeed);
        rightLeader.set(rightSpeed);

        // SALVAR PARA SHUFFLEBOARD
        lastLeftSpeed  = leftSpeed;
        lastRightSpeed = rightSpeed;

        // ----------- CÁLCULO DE VELOCIDADE APROXIMADA (m/s) ------------
        estimatedSpeed = (leftSpeed + rightSpeed) / 2.0 * 4.3;  
        // 4.3 m/s = velocidade típica de robô FRC — ajuste depois

        // ----------- CÁLCULO DE ORIENTAÇÃO (yaw) ------------
        estimatedYaw += rotation * 3.0;  
        // 3° por ciclo → apenas aproximação
    }

    /** Para os motores */
    public void stop() {
        leftLeader.set(0);
        rightLeader.set(0);

        lastLeftSpeed  = 0;
        lastRightSpeed = 0;
        estimatedSpeed = 0;
    }
}
