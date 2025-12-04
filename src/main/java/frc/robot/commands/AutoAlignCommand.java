package frc.robot.commands;

import java.util.Optional;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.VisionSubsystem;

public class AutoAlignCommand extends Command {

    private final DriveSubsystem drive;
    private final VisionSubsystem vision;

    // Ganho de rotação (tuning)
    private static final double kP = 0.03;

    public AutoAlignCommand(DriveSubsystem drive, VisionSubsystem vision) {
        this.drive = drive;
        this.vision = vision;
        addRequirements(drive);
    }

    @Override
    public void execute() {

        Optional<Double> yaw = vision.getBestYaw();

        if (yaw.isEmpty()) {
            // Sem target => não mover
            drive.arcadeDrive(0, 0, false);
            return;
        }

        double error = yaw.get(); // yaw positivo -> tag à direita

        double rotation = error * kP;

        // Limitar rotação
        rotation = Math.max(-0.6, Math.min(0.6, rotation));

        // Apenas gira, sem andar para frente
        drive.arcadeDrive(0, rotation, false);
    }

    @Override
    public void end(boolean interrupted) {
        drive.stop();
    }
}
