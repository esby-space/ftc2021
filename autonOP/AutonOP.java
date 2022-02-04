package org.firstinspires.ftc.teamcode.autonOP;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;

@Autonomous(name="AutonOP", group="Auton")

public class AutonOP extends LinearOpMode {

    /* Declare OpMode members. */
    private DcMotor frontLeftMotor = null;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor linearSlide;
    //  private DcMotor verticalSweeper;
    private DcMotor duckTurner;
    private Servo deliverySystem;
    boolean up = false;
    boolean down =false;
    double elapsedTime;
    private ElapsedTime     runtime = new ElapsedTime();


    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.2;
    static final double     TURN_SPEED              = 0.5;

    @Override
    public void runOpMode() {

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        frontLeftMotor = hardwareMap.dcMotor.get("leftFront");
        frontRightMotor = hardwareMap.dcMotor.get("rightFront");
        backLeftMotor = hardwareMap.dcMotor.get("leftRear");
        backRightMotor = hardwareMap.dcMotor.get("rightRear");
        linearSlide = hardwareMap.dcMotor.get("linearSlide");
        duckTurner = hardwareMap.dcMotor.get("duckSystem");

        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Say", "auton powered up!");    //
        telemetry.update();

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        frontLeftMotor.setMode(RunMode.RUN_USING_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0",  "Starting at %7d :%7d",
                frontRightMotor.getCurrentPosition(),
                frontLeftMotor.getCurrentPosition(),
                backRightMotor.getCurrentPosition(),
                backLeftMotor.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        move1(DRIVE_SPEED, 0.5);
        move2(DRIVE_SPEED,  2.2);
        move4(DRIVE_SPEED, 1.5);
        duckSpin(-0.3, 10);
        move2(DRIVE_SPEED, 2.4);
//        move3(DRIVE_SPEED, 1);


       // robot.leftClaw.setPosition(1.0);            // S4: Stop and close the claw.
       // robot.rightClaw.setPosition(0.0);
       // sleep(1000);     // pause for servos to move

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    /*
     *  Method to perform a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */

    public void move1(double speed,
                      double timeoutS) {


        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // reset the timeout time and start motion.
            runtime.reset();
            frontLeftMotor.setPower(Math.abs(speed));
            frontRightMotor.setPower(Math.abs(speed));
            backLeftMotor.setPower(Math.abs(speed));
            backRightMotor.setPower(Math.abs(speed));


            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) ) {

                // Display it for the driver.
            }

            // Stop all motion;
            frontLeftMotor.setPower(0);
            frontRightMotor.setPower(0);
            backRightMotor.setPower(0);
            backLeftMotor.setPower(0);

            sleep(250);   // optional pause after each move
        }
    }

    public void move2(double speed,
                      double timeoutS) {


        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // reset the timeout time and start motion.
            runtime.reset();
            frontLeftMotor.setPower(-0.2);
            frontRightMotor.setPower(0.15);
            backLeftMotor.setPower(0.1);
            backRightMotor.setPower(-0.2);


            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) ) {

                // Display it for the driver.
            }

            // Stop all motion;
            frontLeftMotor.setPower(0);
            frontRightMotor.setPower(0);
            backRightMotor.setPower(0);
            backLeftMotor.setPower(0);

            sleep(250);   // optional pause after each move
        }
    }

    public void move3(double speed,
                      double timeoutS) {


        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // reset the timeout time and start motion.
            runtime.reset();
            frontLeftMotor.setPower(0.2);
            frontRightMotor.setPower(-0.2);
            backLeftMotor.setPower(-0.2);
            backRightMotor.setPower(0.2);


            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) ) {

                // Display it for the driver.
            }

            // Stop all motion;
            frontLeftMotor.setPower(0);
            frontRightMotor.setPower(0);
            backRightMotor.setPower(0);
            backLeftMotor.setPower(0);

            sleep(250);   // optional pause after each move
        }
    }

    /** move backwards */
    public void move4(double speed,
                      double timeoutS) {


        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // reset the timeout time and start motion.
            runtime.reset();
            frontLeftMotor.setPower(-0.2);
            frontRightMotor.setPower(-0.2);
            backLeftMotor.setPower(-0.2);
            backRightMotor.setPower(-0.2);


            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) ) {

                // Display it for the driver.
            }

            // Stop all motion;
            frontLeftMotor.setPower(0);
            frontRightMotor.setPower(0);
            backRightMotor.setPower(0);
            backLeftMotor.setPower(0);

            sleep(250);   // optional pause after each move
        }
    }

    public void duckSpin(double speed, double timeout) {
        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // reset the timeout time and start motion.
            runtime.reset();
            duckTurner.setPower(speed);


            while (opModeIsActive() &&
                    (runtime.seconds() < timeout) ) {

                // Display it for the driver.
            }
        }
    }

    public void move5(double speed,
                      double timeoutS) {


        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // reset the timeout time and start motion.
            runtime.reset();
            frontLeftMotor.setPower(0.2);
            frontRightMotor.setPower(-0.15);
            backLeftMotor.setPower(-0.1);
            backRightMotor.setPower(0.2);


            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS)) {

                // Display it for the driver.
            }

            // Stop all motion;
            frontLeftMotor.setPower(0);
            frontRightMotor.setPower(0);
            backRightMotor.setPower(0);
            backLeftMotor.setPower(0);

            sleep(250);   // optional pause after each move
        }
    }
}
