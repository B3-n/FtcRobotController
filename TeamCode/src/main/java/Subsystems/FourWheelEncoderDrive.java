package Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.bosch.BHI260IMU;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class FourWheelEncoderDrive extends SubsystemBase {

    Motor frontLeft, backLeft, frontRight, backRight;
    // MotorEx.Encoder frontLeft_encoder, backLeft_encoder, frontRight_encoder, backRight_encoder;
    private IMU imu;
    // private MecanumDriveOdometry odometry;

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    HardwareMap m_hardwareMap;

    public FourWheelEncoderDrive(HardwareMap hardwareMap) {
        m_hardwareMap = hardwareMap;
    }

    public void initDriveWheels() {
        frontLeft = new Motor(m_hardwareMap, "FrontLeft", Motor.GoBILDA.RPM_312);
        frontRight = new Motor(m_hardwareMap, "FrontRight", Motor.GoBILDA.RPM_312);
        backLeft = new Motor(m_hardwareMap, "BackLeft", Motor.GoBILDA.RPM_312);
        backRight = new Motor(m_hardwareMap, "BackRight", Motor.GoBILDA.RPM_312);

        frontLeft.setInverted(true);
        backLeft.setInverted(true);

        frontLeft.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.stopAndResetEncoder();
        frontRight.stopAndResetEncoder();
        backLeft.stopAndResetEncoder();
        backRight.stopAndResetEncoder();

        frontLeft.setRunMode(Motor.RunMode.VelocityControl);
        frontRight.setRunMode(Motor.RunMode.VelocityControl);
        backLeft.setRunMode(Motor.RunMode.VelocityControl);
        backRight.setRunMode(Motor.RunMode.VelocityControl);

        frontLeft.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        frontLeft.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void initIMU() {
        this.imu = m_hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.RIGHT));
        imu.initialize(parameters);
    }

    // Failed to set up Odometry
    /*public void odometrySetup() {
        // Adjust these numbers based on wheel diameter and motor gear ratio
        double distancePerPulse = (Math.PI * 3.78) / 537.7;
        odometry = new MecanumDriveOdometry(kinematics, Rotation2d.fromDegrees(getHeading()));
    }

    public void periodic() {}*/

    public double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    public void encoderDrive(double speed, double lfInches, double rfInches, double lbInches, double rbInches) {
        int lfTarget;
        int rfTarget;
        int lbTarget;
        int rbTarget;

        // Determine new target position, and pass to motor controller
        lfTarget = frontLeft.getCurrentPosition() + (int)(lfInches * COUNTS_PER_INCH);
        rfTarget = frontRight.getCurrentPosition() + (int)(rfInches * COUNTS_PER_INCH);
        lbTarget = backLeft.getCurrentPosition() + (int)(lbInches * COUNTS_PER_INCH);
        rbTarget = backRight.getCurrentPosition() + (int)(rbInches * COUNTS_PER_INCH);

        frontLeft.setTargetPosition(lfTarget);
        frontRight.setTargetPosition(rfTarget);
        backLeft.setTargetPosition(lbTarget);
        backRight.setTargetPosition(rbTarget);

        // Turn On PositionControl
        frontLeft.setRunMode(Motor.RunMode.PositionControl);
        frontRight.setRunMode(Motor.RunMode.PositionControl);
        backLeft.setRunMode(Motor.RunMode.PositionControl);
        backRight.setRunMode(Motor.RunMode.PositionControl);

        // reset the timeout time and start motion.
        frontLeft.set(Math.abs(speed));
        frontRight.set(Math.abs(speed));
        backLeft.set(Math.abs(speed));
        backRight.set(Math.abs(speed));
    }

    public boolean isDriveFinished() {
        return frontLeft.motor.isBusy() && frontRight.motor.isBusy() && backLeft.motor.isBusy() && backRight.motor.isBusy();
    }

    public void disableEncoderDrive() {
        // Stop all motion;
        frontLeft.set(0);
        frontRight.set(0);
        backLeft.set(0);
        backRight.set(0);

        // Turn off PositionControl
        frontLeft.setRunMode(Motor.RunMode.VelocityControl);
        frontRight.setRunMode(Motor.RunMode.VelocityControl);
        backLeft.setRunMode(Motor.RunMode.VelocityControl);
        backRight.setRunMode(Motor.RunMode.VelocityControl);
    }
}
