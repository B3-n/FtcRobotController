package OpModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.arcrobotics.ftclib.command.CommandOpMode;

import Subsystems.FourWheelEncoderDrive;
import Commands.PathPlanner;

@Autonomous(name = "Follow Path", group = "Autonomous")
public class FollowPath extends CommandOpMode{
    private FourWheelEncoderDrive driveSubsystem;
    private PathPlanner pathPlanner;

    @Override
    public void initialize() {
        driveSubsystem = new FourWheelEncoderDrive();
        pathPlanner = new PathPlanner(driveSubsystem);
        schedule(pathPlanner);
    }
}
