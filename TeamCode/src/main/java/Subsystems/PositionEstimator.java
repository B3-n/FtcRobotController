package Subsystems;

import static Subsystems.FourWheelEncoderDrive.ENCODER_COUNTS_PER_INCH;

public class PositionEstimator {

    FourWheelEncoderDrive m_driveSubsystem;
    double prevXEncoder;
    double prevYEncoder;
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

        double xDistance  = m_driveSubsystem.xEncoder.getCurrentPosition();
        double yDistance = m_driveSubsystem.yEncoder.getCurrentPosition();

        prevXEncoder = xDistance;
        prevYEncoder = yDistance;
        
        xDistance /= ENCODER_COUNTS_PER_INCH;
        yDistance /= ENCODER_COUNTS_PER_INCH;

        xPosition += yDistance * Math.cos(m_angle) + xDistance * Math.cos(m_angle - Math.PI / 2);
        yPosition += yDistance * Math.sin(m_angle) + xDistance * Math.sin(m_angle - Math.PI / 2);

        double[] position;
        position = new double[2];

        position[0] = xPosition;
        position[1] = yPosition;

        return position;
    }
}
