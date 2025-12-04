package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;

import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.commands.JoystickDriveCommand;
import frc.robot.commands.AlignToAprilTagCommand;
import frc.robot.subsystems.DriveSubsystem;

import org.photonvision.PhotonCamera;

public class RobotContainer {

    // ========== SUBSISTEMAS ==========
    private final DriveSubsystem driveSubsystem = new DriveSubsystem();

    // ========== CONTROLE ==========
    private final Joystick joystick = new Joystick(0);

    // ========== CÂMERAS ==========
    private final PhotonCamera aprilTagCamera = new PhotonCamera("aprilcam");  // PhotonVision

    // ========== SHUFFLEBOARD ==========
    private final ShuffleboardTab driveTab = Shuffleboard.getTab("Driver");
    private final ShuffleboardTab visionTab = Shuffleboard.getTab("Vision");

    public RobotContainer() {

        // =====================================
        // DEFAULT COMMAND → DIRIGIR COM JOYSTICK
        // =====================================
        driveSubsystem.setDefaultCommand(
            new JoystickDriveCommand(driveSubsystem, joystick)
        );

        // =====================================
        //  CAMERA DO PILOTO (USB)
        // =====================================
        UsbCamera driverCam = CameraServer.startAutomaticCapture(0);
        driverCam.setFPS(30);
        driverCam.setResolution(320, 240);

        driveTab.add("Driver Camera", driverCam)
                .withSize(4, 3);

        // =====================================
        //  STREAM DA CÂMERA PHOTONVISION
        // =====================================
        visionTab.add("AprilTag Camera", aprilTagCamera)
                .withSize(4, 3);

        // =====================================
        //  MÉTRICAS DO PHOTONVISION
        // =====================================

        visionTab.addBoolean(
            "Tem Alvo?",
            () -> aprilTagCamera.getLatestResult().hasTargets()
        );

        visionTab.addNumber(
            "Yaw (tx)",
            () -> aprilTagCamera.getLatestResult().hasTargets()
                ? aprilTagCamera.getLatestResult().getBestTarget().getYaw()
                : 0
        );

        visionTab.addNumber(
            "Pitch (ty)",
            () -> aprilTagCamera.getLatestResult().hasTargets()
                ? aprilTagCamera.getLatestResult().getBestTarget().getPitch()
                : 0
        );

        visionTab.addNumber(
            "Área",
            () -> aprilTagCamera.getLatestResult().hasTargets()
                ? aprilTagCamera.getLatestResult().getBestTarget().getArea()
                : 0
        );

        // Timestamp substitui latência (PhotonVision 2024 removeu getLatencyMillis)
        visionTab.addNumber(
            "Timestamp (ms)",
            () -> aprilTagCamera.getLatestResult().getTimestampSeconds() * 1000
        );

        // =====================================
        //  GANHOS AJUSTÁVEIS NO SHUFFLEBOARD
        // =====================================
        visionTab.add("kP Girar", 0.02);
        visionTab.add("MinPower", 0.05);

        // =====================================
        //  BINDS DO JOYSTICK
        // =====================================
        configureBindings();
    }

    // ============================================================
    // BINDINGS DO CONTROLE
    // ============================================================
    private void configureBindings() {

        // Botão 2 → Alinhamento automático com AprilTag
        new JoystickButton(joystick, 2)
            .whileTrue(
                new AlignToAprilTagCommand(
                    driveSubsystem,
                    aprilTagCamera
                )
            );
    }
}
