package Commands;

import com.arcrobotics.ftclib.command.CommandBase;
import com.qualcomm.robotcore.util.ElapsedTime;


import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;

import Subsystems.FourWheelEncoderDrive;
import Utils.PIDController;

public class PointDrive extends CommandBase {

    ElapsedTime runtime = new ElapsedTime();

    // Place April Tag Processor Here.

    private Camera camera;

    private FourWheelEncoderDrive fourWheelEncoderDrive;

    Subsystems.FourWheelEncoderDrive m_drive;
    double m_speed, m_xTarget, m_yTarget, m_turn, m_timeoutS;

    PIDController xControl = new PIDController(0.1, 0.0, 0.0);
    PIDController yControl = new PIDController(0.1, 0.0, 0.0);

    double xRobotPosition = 0.0;
    double yRobotPosition = 0.0;

    public PointDrive(FourWheelEncoderDrive drive, double speed, double xTarget, double yTarget, double turn, double timeoutS) {
        m_drive = drive;
        m_speed = speed;
        m_xTarget = xTarget;
        m_yTarget = yTarget;
        m_turn = turn;
        m_timeoutS = timeoutS;
    }

    @Override
    public void initialize() {
        FourWheelEncoderDrive drive = new FourWheelEncoderDrive();
        drive.initDriveWheels();
        drive.initIMU();

        runtime.reset();

        double heading = m_drive.getHeading();
        double x = xControl.calculate(m_xTarget, xRobotPosition, new ElapsedTime());
        double y = yControl.calculate(m_yTarget, yRobotPosition, new ElapsedTime());

        // Add Turn PID here

        // Rotate input vector by robot heading to achieve field-centric control
        double rotatedX = x * Math.cos(heading) + y * Math.sin(heading);
        double rotatedY = -x * Math.sin(heading) + y * Math.cos(heading);

        // Calculate individual motor powers
        double leftFront = rotatedY + rotatedX + m_turn;
        double rightFront = rotatedY - rotatedX - m_turn;
        double leftBack = rotatedY - rotatedX + m_turn;
        double rightBack = rotatedY + rotatedX - m_turn;

        // Normalize motor powers to prevent exceeding maximum power
        double maxPower = Math.max(Math.abs(leftFront), Math.max(Math.abs(rightFront),
                Math.max(Math.abs(leftBack), Math.abs(rightBack))));
        if (maxPower > 1) {
            leftFront /= maxPower;
            rightFront /= maxPower;
            leftBack /= maxPower;
            rightBack /= maxPower;
        }

        m_drive.encoderDrive(m_speed, leftFront, rightFront, leftBack, rightBack);
    }

    public boolean isFinished() {
        return m_drive.isDriveFinished() || runtime.seconds() > m_timeoutS;
    }

    public void end(boolean interrupted) {
        m_drive.disableEncoderDrive();
    }

    public void run() {}
}