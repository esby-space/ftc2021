package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;

@Autonomous(name = "AutonOP Untested", group = "Disco Bot")
public class AutonOPNew extends LinearOpMode {

    // MOTORS AND SERVOS//
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor linearSlide;
    private DcMotor intakeSystem;
    private DcMotor duckWheel;
    private Servo deliverySystem;

    static final double COUNTS_PER_MOTOR_REV = 1440; // eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 2.0; // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 4.0; // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double DRIVE_SPEED = 0.6;
    static final double TURN_SPEED = 0.5;

    @Override
    public void runOpMode() {

        // GET MOTORS //
        // control hub
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // expansion hub
        linearSlide = hardwareMap.dcMotor.get("linearSlide");
        intakeSystem = hardwareMap.dcMotor.get("intakeSystem");
        duckWheel = hardwareMap.dcMotor.get("duckSystem");

        // servo
        deliverySystem = hardwareMap.servo.get("deliverySystem");

        // ENCODERS //
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0", "Starting at %7d :%7d",
                frontLeft.getCurrentPosition(),
                frontRight.getCurrentPosition(),
                backRight.getCurrentPosition(),
                backRight.getCurrentPosition());
        telemetry.update();

        // START //
        waitForStart();

        // PATH //
        encoderDrive(DRIVE_SPEED, 48, 48, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        encoderDrive(TURN_SPEED, 12, -12, 4.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
        encoderDrive(DRIVE_SPEED, -24, -24, 4.0);  // S3: Reverse 24 Inches with 4 Sec timeout

        telemetry.addData("Path", "done!");
        telemetry.update();
    }

    /**
     * Perform a relative move based on encoder counts.
     * Encoders are not reset as the move is based on the current position.
     *
     * @param speed       how fast to move the motors
     * @param leftInches  inches to run the left motors
     * @param rightInches inches to run the right motors
     * @param timeOut     seconds the motor will run before being forced to stop
     */
    public void encoderDrive(double speed, double leftInches, double rightInches, double timeOut) {
        if (opModeIsActive()) {
            int leftTarget = frontLeft.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            int rightTarget = frontRight.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            frontLeft.setTargetPosition(leftTarget);
            frontRight.setTargetPosition(rightTarget);

            // turn on run_to_position
            frontLeft.setMode(RunMode.RUN_TO_POSITION);
            frontRight.setMode(RunMode.RUN_TO_POSITION);
            backLeft.setMode(RunMode.RUN_TO_POSITION);
            backRight.setMode(RunMode.RUN_TO_POSITION);

            // timeout timer setup
            ElapsedTime runtime = new ElapsedTime();
            runtime.reset();

            // activate the motors!
            frontLeft.setPower(Math.abs(speed));
            frontRight.setPower(Math.abs(speed));
            backLeft.setPower(Math.abs(speed));
            backRight.setPower(Math.abs(speed));

            // while the robot is active, time out not activated, and both motors are running
            while (opModeIsActive() && runtime.seconds() < timeOut && frontLeft.isBusy() && frontLeft.isBusy()) {
                telemetry.addData("Path 1", "Running to %7d :%7d", leftTarget, rightTarget);
                telemetry.addData("Path 2", "Running at %7d :%7d",
                        frontLeft.getCurrentPosition(),
                        frontRight.getCurrentPosition(),
                        backLeft.getCurrentPosition(),
                        backRight.getCurrentPosition());
                telemetry.update();
            }

            // stop the motors
            frontLeft.setPower(0);
            frontRight.setPower(0);
            backLeft.setPower(0);
            backRight.setPower(0);

            // turn off run_to_position
            frontLeft.setMode(RunMode.RUN_USING_ENCODER);
            backRight.setMode(RunMode.RUN_USING_ENCODER);
            frontRight.setMode(RunMode.RUN_USING_ENCODER);
            backLeft.setMode(RunMode.RUN_USING_ENCODER);

            // optional rest between moves
            sleep(250);
        }
    }

    /** Move the linear slide to a absolute specific level using encoders.
     *
     * @param level which level to move to
     * @param timeOut how long to run the motor before forced to stop */
    public void encoderLinearSlide(int level, double timeOut) {
        if (opModeIsActive()) {
            int target;
            switch (level) {
                case 1:
                    target = 5; // TODO: FIND REAL NUMBERS
                    break;
                case 2:
                    target = 10;
                    break;
                case 3:
                    target = 100;
                    break;
                default:
                    target = 0;
            }
            linearSlide.setTargetPosition(target);

            linearSlide.setMode(RunMode.RUN_TO_POSITION);
            ElapsedTime timer = new ElapsedTime();
            timer.reset();
            linearSlide.setPower(1);

            while (opModeIsActive() && timer.seconds() < timeOut && linearSlide.isBusy()) {
                telemetry.addData("Linear Slide", "Going to %7d", target);
                telemetry.addData("Linear Slide", "Running at %7d", linearSlide.getCurrentPosition());
                telemetry.update();
            }
            linearSlide.setPower(0);
            linearSlide.setMode(RunMode.RUN_USING_ENCODER);
        }
    }
}
