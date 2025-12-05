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
    
        var results = camera.getAllUnreadResults();
    
        // Nenhum frame novo recebido
        if (results.isEmpty()) {
            drive.arcadeDrive(0, 0, false);
            return;
        }
    
        // Pega o último resultado recebido (o mais recente)
        var result = results.get(results.size() - 1);
    
        if (!result.hasTargets()) {
            drive.arcadeDrive(0, 0, false);
            return;
        }
    
        PhotonTrackedTarget target = result.getBestTarget();
        double yaw = target.getYaw();
    
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
