package Subsystems;

import static Subsystems.FourWheelEncoderDrive.COUNTS_PER_INCH;

public class PositionEstimator {

    FourWheelEncoderDrive m_driveSubsystem;
    double prevFrontLeftEncoder;
    double prevFrontRightEncoder;
    double prevBackLeftEncoder;
    double prevBackRightEncoder;
    double xPosition;
    double yPosition;
    double m_angle;

    public double[] updatePosition(FourWheelEncoderDrive driveSubsystem, double angle) {
        m_driveSubsystem = driveSubsystem;
        m_angle = angle;
        m_angle += 360 + 90;
        m_angle %= 360;
        m_angle /= 360;
        m_angle *= 2 * Math.PI;

        double frontLeftDistance = m_driveSubsystem.frontLeft.getCurrentPosition();
        double frontRightDistance = m_driveSubsystem.frontRight.getCurrentPosition();
        double backRightDistance = m_driveSubsystem.backRight.getCurrentPosition();
        double backLeftDistance = m_driveSubsystem.backLeft.getCurrentPosition();

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
        xPosition += yDistance * Math.cos(m_angle) + xDistance * Math.cos(m_angle - Math.PI / 2);
        yPosition += yDistance * Math.sin(m_angle) + xDistance * Math.sin(m_angle - Math.PI / 2);

        double[] position;
        position = new double[2];

        position[0] = xPosition;
        position[1] = yPosition;

        return position;
    }
}
