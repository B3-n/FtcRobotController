package Commands;

import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import Subsystems.FourWheelEncoderDrive;

public class PathPlanner extends SequentialCommandGroup {
    public PathPlanner(FourWheelEncoderDrive driveSubsystem) {
        addCommands(
                /* Place movements here:
                new PointDrive(
                 subsystem(driveSubsystem),
                 speed(-1 to 1),
                 target(inches)(left/right),
                 target(inches)(up/down),
                 turn(degrees),
                 timeout(seconds)
                ) */

                new PointDrive(driveSubsystem, 0.5, 10, 10, 0, 40)
        );
    }
}
