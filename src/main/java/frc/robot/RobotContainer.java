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
    private final PhotonCamera aprilTagCamera = new PhotonCamera("aprilcam");

    // ========== SHUFFLEBOARD ==========
    private final ShuffleboardTab driveTab = Shuffleboard.getTab("Driver");
    private final ShuffleboardTab visionTab = Shuffleboard.getTab("Vision");

    public RobotContainer() {

        // =====================================================================
        // Default: dirigir com joystick
        // =====================================================================
        driveSubsystem.setDefaultCommand(
            new JoystickDriveCommand(driveSubsystem, joystick)
        );

        // =====================================================================
        // Camera USB do piloto
        // =====================================================================
        UsbCamera driverCam = CameraServer.startAutomaticCapture(0);
        driverCam.setFPS(30);
        driverCam.setResolution(320, 240);

        driveTab.add("Driver Camera", driverCam).withSize(4, 3);

        // =====================================================================
        // Stream da camera PhotonVision
        // =====================================================================
        visionTab.add("AprilTag Camera", aprilTagCamera).withSize(4, 3);

        // =====================================================================
        // Função correta para pegar o último resultado
        // =====================================================================
        visionTab.addBoolean("Tem Alvo?", () -> {
            var list = aprilTagCamera.getAllUnreadResults();
            if (list.isEmpty()) return false;
            return list.get(list.size() - 1).hasTargets();
        });

        visionTab.addNumber("Yaw (tx)", () -> {
            var list = aprilTagCamera.getAllUnreadResults();
            if (list.isEmpty()) return 0;
            var r = list.get(list.size() - 1);
            return r.hasTargets() ? r.getBestTarget().getYaw() : 0;
        });

        visionTab.addNumber("Pitch (ty)", () -> {
            var list = aprilTagCamera.getAllUnreadResults();
            if (list.isEmpty()) return 0;
            var r = list.get(list.size() - 1);
            return r.hasTargets() ? r.getBestTarget().getPitch() : 0;
        });

        visionTab.addNumber("Área", () -> {
            var list = aprilTagCamera.getAllUnreadResults();
            if (list.isEmpty()) return 0;
            var r = list.get(list.size() - 1);
            return r.hasTargets() ? r.getBestTarget().getArea() : 0;
        });

        visionTab.addNumber("Timestamp (ms)", () -> {
            var list = aprilTagCamera.getAllUnreadResults();
            if (list.isEmpty()) return 0;
            return list.get(list.size() - 1).getTimestampSeconds() * 1000;
        });

        // =====================================================================
        // Ajustes de alinhamento no Shuffleboard
        // =====================================================================
        visionTab.add("kP Girar", 0.02);
        visionTab.add("MinPower", 0.05);

        configureBindings();
    }

    // =====================================================================
    // Binds do Controle
    // =====================================================================
    private void configureBindings() {

        new JoystickButton(joystick, 2)
            .whileTrue(
                new AlignToAprilTagCommand(
                    driveSubsystem,
                    aprilTagCamera
                )
            );
    }
}
