package Utils;

import com.qualcomm.robotcore.util.ElapsedTime;

public class TurnPID extends PIDController {
    public TurnPID(double kP, double kI, double kD) {
        super(kP, kI, kD);
    }

    @Override
    public double calculate(double turnTarget, double heading, ElapsedTime timer) {
        double error = turnTarget - heading;

        while (error > Math.PI) error -= 2 * Math.PI;
        while (error < -Math.PI) error += 2 * Math.PI;

        double deltaTime = timer.seconds();
        timer.reset();

        return super.calculate(error, 0, new FakeTimer(deltaTime));
    }

    private static class FakeTimer extends ElapsedTime {
        private final double fixedTime;
        public FakeTimer(double fixedTime) { this.fixedTime = fixedTime; }
        @Override public double seconds() { return fixedTime; }
        @Override public void reset() {}
    }
}
