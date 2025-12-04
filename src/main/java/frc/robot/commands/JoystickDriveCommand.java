package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.MathUtils;
import frc.robot.subsystems.DriveSubsystem;

public class JoystickDriveCommand extends Command {

    private final DriveSubsystem drive;
    private final Joystick joystick;

    // Configurações
    private static final double DEADBAND = 0.08;
    private static final double EXPO_FORWARD = 0.15;  // curva leve
    private static final double EXPO_ROTATION = 0.25; // curva mais forte para facilitar dirigir reto

    public JoystickDriveCommand(DriveSubsystem drive, Joystick joystick) {
        this.drive = drive;
        this.joystick = joystick;
        addRequirements(drive);
    }

    @Override 
    public void execute() {

        double forward  = -joystick.getRawAxis(1);
        double rotation =  joystick.getRawAxis(4);

        boolean boost = joystick.getRawButton(1);

        // --- Deadband inteligente ---
        forward  = MathUtils.smartDeadband(forward, DEADBAND);
        rotation = MathUtils.smartDeadband(rotation, DEADBAND);

        // --- Curva suave ---
        forward  = MathUtils.expoCurve(forward, EXPO_FORWARD);
        rotation = MathUtils.expoCurve(rotation, EXPO_ROTATION);

        // Envia ao drivetrain
        drive.arcadeDrive(forward, rotation, boost);
    }

    @Override
    public void end(boolean interrupted) {
        drive.stop();
    }
}
