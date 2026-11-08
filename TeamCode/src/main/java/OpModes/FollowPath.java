package OpModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.arcrobotics.ftclib.command.CommandOpMode;

import Subsystems.FourWheelEncoderDrive;
import Commands.PathPlanner;
import Subsystems.PositionEstimator;

@Autonomous(name = "Follow Path", group = "Autonomous")
public class FollowPath extends CommandOpMode{

    private FourWheelEncoderDrive driveSubsystem;
    private PathPlanner pathPlanner;

    @Override
    public void initialize() {
        driveSubsystem = new FourWheelEncoderDrive(hardwareMap);
        Subsystems.PositionEstimator positionEstimator = new PositionEstimator();

        driveSubsystem.initDriveWheels();
        driveSubsystem.initIMU();

        waitForStart();

        pathPlanner = new PathPlanner(positionEstimator, driveSubsystem);
        schedule(pathPlanner);
    }
}
