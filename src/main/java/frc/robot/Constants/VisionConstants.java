package frc.robot.Constants;

import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.apriltag.AprilTagFieldLayout;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.numbers.N1;

import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;

public final class VisionConstants {

    /** Nome da câmera configurada no PhotonVision */
    public static final String kCameraName = "C270_HD_WEBCAM";

    /** Layout oficial de AprilTags */
    public static final AprilTagFieldLayout kTagLayout =
        AprilTagFields.k2025ReefscapeAndyMark.loadAprilTagLayoutField();

    /** Transformo da câmera no robô */
    public static final Transform3d kRobotToCam = new Transform3d(
            new Translation3d(
                0.15,   // X → Para frente do robô (metros)
                0.00,   // Y → Para esquerda do robô (positivo)
                0.09    // Z → Altura da câmera (m)
            ),
            new Rotation3d(
                0,      // pitch
                0,      // yaw
                0       // roll
            )
        );

    /** StdDev quando apenas 1 tag está visível */
    public static final Matrix<N3, N1> kSingleTagStdDevs =
        VecBuilder.fill(4, 4, 8); // metros e radianos — PhotonVision recommended

    /** StdDev quando múltiplas tags estão visíveis */
    public static final Matrix<N3, N1> kMultiTagStdDevs =
        VecBuilder.fill(0.5, 0.5, 1); // Muito mais preciso com multitags
}
