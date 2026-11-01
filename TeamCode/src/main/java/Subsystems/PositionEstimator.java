package Subsystems;

import static Subsystems.FourWheelEncoderDrive.COUNTS_PER_INCH;

public class PositionEstimator {

    FourWheelEncoderDrive driveSubsystem;
    double prevFrontLeftEncoder;
    double prevFrontRightEncoder;
    double prevBackLeftEncoder;
    double prevBackRightEncoder;
    double xPosition;
    double yPosition;

    public double[] updatePosition() {
        double angle = driveSubsystem.getHeading();
        angle += 360 + 90;
        angle %= 360;

        double frontLeftDistance = driveSubsystem.frontLeft.getCurrentPosition();
        double frontRightDistance = driveSubsystem.frontRight.getCurrentPosition();
        double backRightDistance = driveSubsystem.backRight.getCurrentPosition();
        double backLeftDistance = driveSubsystem.backLeft.getCurrentPosition();

        double xDistance  = - (frontLeftDistance - prevFrontLeftEncoder)
                + (frontRightDistance - prevFrontRightEncoder)
                + (backLeftDistance  - prevBackLeftEncoder)
                - (backRightDistance - prevBackRightEncoder);
        double yDistance = (frontLeftDistance - prevFrontLeftEncoder)
                + (frontRightDistance - prevFrontRightEncoder)
                + (backLeftDistance  - prevBackLeftEncoder)
                + (backRightDistance - prevBackRightEncoder);

        xDistance /= 4;
        yDistance /= 4;
        xDistance /= COUNTS_PER_INCH;
        yDistance /= COUNTS_PER_INCH;

        prevFrontLeftEncoder = frontLeftDistance;
        prevFrontRightEncoder = frontRightDistance;
        prevBackLeftEncoder = backLeftDistance;
        prevBackRightEncoder = backRightDistance;
        xPosition += yDistance * Math.cos(angle) + xDistance * Math.cos(angle - 90);
        yPosition += yDistance * Math.sin(angle) + xDistance * Math.sin(angle - 90);

        double []position = {
                xPosition,
                yPosition
        };

        return position;
    }
}
