package Utils;

import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDController {

    private  double kP; // Proportional gain
    private  double kI; // Integral gain
    private  double kD; // Derivative gain

    private double lastError = 0; // Previous error value
    private double errorSum = 0; // Accumulated error over time

    /**
     * Constructor for PIDController.
     * Initializes the PID gains.
     *
     * @param kP The proportional gain.
     * @param kI The integral gain.
     * @param kD The derivative gain.
     */
    public PIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    /**
     * Calculates the output of the PID controller based on the target and current position.
     *
     * @param target          The desired target position.
     * @param currentPosition The current position of the system.
     * @param timer           The timer used to calculate the time elapsed since the last update.
     * @return The calculated output of the PID controller.
     */
    public double calculate(double target, double currentPosition, ElapsedTime timer) {
        // Calculate error
        double error = target - currentPosition;

        // Calculate integral term
        errorSum += error * timer.seconds();

        // Calculate derivative term
        double errorRate = (error - lastError) / timer.seconds();

        // Calculate PID output
        double output = kP * error + kI * errorSum + kD * errorRate;

        // Update previous error
        lastError = error;

        // Reset timer
        timer.reset();

        return output;
    }
    public void setPID(Double kP, Double kI, Double kD){
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }
}