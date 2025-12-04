package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DriveSubsystem;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class AlignToAprilTagCommand extends Command {

    private final DriveSubsystem drive;
    private final PhotonCamera camera;

    private final ShuffleboardTab tab = Shuffleboard.getTab("Vision");

    public AlignToAprilTagCommand(DriveSubsystem drive, PhotonCamera cam) {
        this.drive = drive;
        this.camera = cam;

        addRequirements(drive);
    }

    @Override
    public void execute() {

        var result = camera.getLatestResult();

        if (!result.hasTargets()) {
            drive.arcadeDrive(0, 0, false);
            return;
        }

        PhotonTrackedTarget target = result.getBestTarget();
        double yaw = target.getYaw();

        // Recuperando valores do Shuffleboard
        double kP = tab.getLayout("Calibration", "List")
                        .add("kP", 0.02).getEntry().getDouble(0.02);
        double minPower = tab.getLayout("Calibration", "List")
                        .add("MinPower", 0.05).getEntry().getDouble(0.05);

        double turnPower = kP * yaw;

        if (Math.abs(turnPower) < minPower) {
            turnPower = Math.signum(turnPower) * minPower;
        }

        drive.arcadeDrive(0, -turnPower, false);
    }

    @Override
    public void end(boolean interrupted) {
        drive.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
