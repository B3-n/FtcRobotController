package Commands;

import com.arcrobotics.ftclib.command.CommandBase;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;


import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;

import Subsystems.FourWheelEncoderDrive;
import Subsystems.PositionEstimator;

import Utils.PIDController;
import Utils.TurnPID;

public class PointDrive extends CommandBase {

    ElapsedTime runtime = new ElapsedTime();
    private Camera camera;

    Subsystems.FourWheelEncoderDrive m_drive;

    PositionEstimator m_positionEstimator;

    double m_speed, m_xTarget, m_yTarget, m_turnTarget, m_timeoutS;

    PIDController xControl = new PIDController(0.1, 0.0, 0.0);
    PIDController yControl = new PIDController(0.1, 0.0, 0.0);
    TurnPID turnControl = new TurnPID(0.1, 0.0, 0.0);

    public PointDrive(PositionEstimator positionEstimator, FourWheelEncoderDrive drive, double speed, double xTarget, double yTarget, double turnTarget, double timeoutS) {
        m_positionEstimator = positionEstimator;
        m_drive = drive;
        m_speed = speed;
        m_xTarget = xTarget;
        m_yTarget = yTarget;
        m_turnTarget = turnTarget;
        m_timeoutS = timeoutS;
    }

    public double xRobotPosition, yRobotPosition, heading, x, y, turn, leftFront, rightFront, leftBack, rightBack;



    @Override
    public void initialize() {

        runtime.reset();

        heading = m_drive.getHeading();
        double[] position = m_positionEstimator.updatePosition(m_drive, heading);
        xRobotPosition = position[0];
        yRobotPosition = position[1];

        x = xControl.calculate(m_xTarget, xRobotPosition, new ElapsedTime());
        y = yControl.calculate(m_yTarget, yRobotPosition, new ElapsedTime());
        turn = turnControl.calculate(m_turnTarget, heading, new ElapsedTime());

        double robotR = 10.75;
        double turnD = Math.toRadians(turn) * robotR;

        // Rotate input vector by robot heading to achieve field-centric control
        double rotatedX = x * Math.cos(heading) + y * Math.sin(heading);
        double rotatedY = -x * Math.sin(heading) + y * Math.cos(heading);

        // Calculate individual motor powers
        leftFront = rotatedY + rotatedX + turnD;
        rightFront = rotatedY - rotatedX - turnD;
        leftBack = rotatedY - rotatedX + turnD;
        rightBack = rotatedY + rotatedX - turnD;

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