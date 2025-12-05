package frc.robot.subsystems;

import java.util.List;
import java.util.Optional;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class VisionSubsystem extends SubsystemBase {

    private final PhotonCamera camera = new PhotonCamera("photon");

    // NetworkTables para debug no Shuffleboard
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

    // Função correta para ler o último resultado REAL recebido
    private PhotonPipelineResult getLatestUnread() {
        List<PhotonPipelineResult> results = camera.getAllUnreadResults();

        if (results.isEmpty())
            return new PhotonPipelineResult(); // vazio (sem targets)

        return results.get(results.size() - 1); // último resultado
    }

    // Retornar o YAW do melhor target
    public Optional<Double> getBestYaw() {

        var result = getLatestUnread();

        if (!result.hasTargets()) 
            return Optional.empty();

        return Optional.of(result.getBestTarget().getYaw());
    }

    // PERIODIC — atualizar NetworkTables
    @Override
    public void periodic() {

        var result = getLatestUnread();

        // Está recebendo dados?
        connectedEntry.setBoolean(result != null);

        if (result.hasTargets()) {

            PhotonTrackedTarget t = result.getBestTarget();

            txEntry.setDouble(t.getYaw());
            tyEntry.setDouble(t.getPitch());
            areaEntry.setDouble(t.getArea());
            hasTargetsEntry.setBoolean(true);

            targetsEntry.setDouble(result.getTargets().size());

            latencyEntry.setDouble(result.getTimestampSeconds() * 1000.0);
            timestampEntry.setDouble(result.getTimestampSeconds() * 1000.0);

            double distance = estimateDistance(t.getPitch());
            distanceEntry.setDouble(distance);

        } else {
            hasTargetsEntry.setBoolean(false);
        }
    }

    // Estimativa simples de distância
    private double estimateDistance(double pitchDeg) {

        double cameraHeight = 0.70; // metros
        double targetHeight = 1.34; // metros
        double cameraAngle = 23;    // graus

        double angle = Math.toRadians(cameraAngle + pitchDeg);

        return (targetHeight - cameraHeight) / Math.tan(angle);
    }

    // Apenas para compatibilidade
    public String getVisionCameraStream() {
        return "photon";
    }

    public String getDriverCameraStream() {
        return "driver_cam";
    }
}
