package frc.robot.subsystems;

import java.util.Optional;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonTrackedTarget;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class VisionSubsystem extends SubsystemBase {

    private final PhotonCamera camera = new PhotonCamera("photon");

    public final NetworkTableEntry txEntry;
    public final NetworkTableEntry tyEntry;
    public final NetworkTableEntry areaEntry;
    public final NetworkTableEntry latencyEntry;
    public final NetworkTableEntry timestampEntry;
    public final NetworkTableEntry distanceEntry;
    public final NetworkTableEntry hasTargetsEntry;
    public final NetworkTableEntry targetsEntry;
    public final NetworkTableEntry pipelineEntry;
    public final NetworkTableEntry connectedEntry;

    public VisionSubsystem() {
        var table = NetworkTableInstance.getDefault().getTable("Vision");

        txEntry         = table.getEntry("tx");
        tyEntry         = table.getEntry("ty");
        areaEntry       = table.getEntry("area");
        latencyEntry    = table.getEntry("latency");
        timestampEntry  = table.getEntry("timestamp");
        distanceEntry   = table.getEntry("distance");
        hasTargetsEntry = table.getEntry("hasTargets");
        targetsEntry    = table.getEntry("targets");
        pipelineEntry   = table.getEntry("pipeline");
        connectedEntry  = table.getEntry("connected");
    }

    public Optional<Double> getBestYaw() {
    var result = camera.getLatestResult();

    if (!result.hasTargets()) {
        return Optional.empty();
    }

    return Optional.of(result.getBestTarget().getYaw());
}


    @Override
    public void periodic() {
        var result = camera.getLatestResult();

        connectedEntry.setBoolean(result != null);

        if (result.hasTargets()) {

            PhotonTrackedTarget t = result.getBestTarget();

            txEntry.setDouble(t.getYaw());
            tyEntry.setDouble(t.getPitch());
            areaEntry.setDouble(t.getArea());
            hasTargetsEntry.setBoolean(true);

            targetsEntry.setDouble(result.getTargets().size());

            // latência em milissegundos (result.getLatency() retorna segundos)
            latencyEntry.setDouble(result.getTimestampSeconds());

            timestampEntry.setDouble(result.getTimestampSeconds());

            double distance = estimateDistance(t.getPitch());
            distanceEntry.setDouble(distance);
        } else {
            hasTargetsEntry.setBoolean(false);
        }
    }

    private double estimateDistance(double pitchDeg) {
        double cameraHeight = 0.70;
        double targetHeight = 1.34;
        double cameraAngle = 23;

        double angle = Math.toRadians(cameraAngle + pitchDeg);
        return (targetHeight - cameraHeight) / Math.tan(angle);
    }
    
    public String getVisionCameraStream() {
        return "photon";
    }

    public String getDriverCameraStream() {
        return "driver_cam";
    }
}
