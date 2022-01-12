package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "TeleOP old", group = "TeleOP")
public class TeleOPOld extends LinearOpMode {

    @Override
    public void runOpMode() {
        // map the motors to the robot
        // control hub:
        DcMotor frontLeft = hardwareMap.dcMotor.get("frontLeft");
        DcMotor frontRight = hardwareMap.dcMotor.get("frontRight");
        DcMotor backLeft = hardwareMap.dcMotor.get("backLeft");
        DcMotor backRight = hardwareMap.dcMotor.get("backRight");

//        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // expansion hub motors
        DcMotor linearSlide = hardwareMap.dcMotor.get("linearSlide");
        DcMotor intakeSystem = hardwareMap.dcMotor.get("intakeSystem");
        DcMotor duckWheel = hardwareMap.dcMotor.get("duckSystem");

        // expansion hub servos
        Servo deliverySystem = hardwareMap.servo.get("deliverySystem");
        double deliverySystemPosition = 0.2; // initial position
        double rotationAmount = 0.5;

        // wait for start signal to be given
        waitForStart();
        telemetry.addData("Start", "Hello, World!");
        telemetry.update();

        // initialize the timer
        ElapsedTime motorTimer = new ElapsedTime();
        double startTimer = 0;

        deliverySystem.setPosition(deliverySystemPosition);

        // when the start button is pushed, repeat this until end
        while (opModeIsActive()) {

            // Movement and rotation of the robot with mecanum wheels
            double move_y = -gamepad1.left_stick_y; // Remember, this is reversed!
            double move_x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double move_rotate = -gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(move_y) + Math.abs(move_x) + Math.abs(move_rotate), 1);
            double frontLeftPower = (move_y + move_x + move_rotate) / denominator;
            double backLeftPower = (move_y - move_x + move_rotate) / denominator;
            double frontRightPower = (move_y - move_x - move_rotate) / denominator;
            double backRightPower = (move_y + move_x - move_rotate) / denominator;

            frontLeft.setPower(frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);

            // delivery system
            if (gamepad1.left_bumper) {
                deliverySystem.setPosition(deliverySystemPosition + rotationAmount);
                telemetry.addData("Delivery System", "Counter-clockwise");
                telemetry.update();
            } else if (gamepad1.right_bumper) {
                deliverySystem.setPosition(deliverySystemPosition - rotationAmount);
                telemetry.addData("Delivery System", "Clockwise");
                telemetry.update();
            }

            // intake system
            if (gamepad1.dpad_up) {
                intakeSystem.setPower(-1);
            } else if (gamepad1.dpad_down) {
                intakeSystem.setPower(1);
            } else {
                intakeSystem.setPower(0);
            }

            // duck wheel
            double duckWheelCCW = gamepad1.left_trigger;
            double duckWheelCW = gamepad1.right_trigger;

            if (duckWheelCCW > 0) {
                duckWheel.setPower(-duckWheelCCW);
                telemetry.addData("Duck Wheel", "Counter-clockwise");
                telemetry.update();
            } else if (duckWheelCW > 0) {
                duckWheel.setPower(duckWheelCW);
                telemetry.addData("Duck Wheel", "Clockwise");
                telemetry.update();
            } else {
                duckWheel.setPower(0);
            }

            // linear slide
            boolean y_button = gamepad1.y;
            boolean x_button = gamepad1.x;
            boolean a_button = gamepad1.a;
            boolean b_button = gamepad1.b;
            double seconds = 0;
            double rate = 2; // inches for 1 rotation

            // determine the number of seconds from the bottom the linear slide has to run for
            // decimal-ly numbers based on heights of the wedding cake thingy
            if (y_button) {
                seconds = 0.4 / rate;
                startTimer = motorTimer.time();
                telemetry.addData("Linear Slide", "Going to level 1");
            } else if (x_button) {
                seconds = 1.2 / rate;
                startTimer = motorTimer.time();
                telemetry.addData("Linear Slide", "Going to level 2");
            } else if (a_button) {
                seconds = 2.0 / rate;
                startTimer = motorTimer.time();
                telemetry.addData("Linear Slide", "Going to level 3");
            } else if (b_button) {
                linearSlide.setPower(-1);
                telemetry.addData("Linear Slide", "Going down");
            }
            telemetry.update();

            while (motorTimer.time() < startTimer + seconds) {
                linearSlide.setPower(1);
            }
            linearSlide.setPower(0);

            idle();
        }
    }
}
