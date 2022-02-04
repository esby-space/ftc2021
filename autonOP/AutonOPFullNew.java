package org.firstinspires.ftc.teamcode.autonOP;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@Autonomous(name = "AutonOP Full New", group = "Auton")
public class AutonOPFullNew extends LinearOpMode {

    // MOTORS AND SERVOS//
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private final int MOTOR_TICK_COUNT = 1440;
    private final double WHEEL_DIAMETER = 3.93;
    private final double WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER * 3.14159;

    // TENSORFLOW AND VUFORIA //
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker"
    };

    private static final String VUFORIA_KEY =
            "ASFtnEj/////AAABmTdt18pG5U+iioqvGlhBis+Itb3ank1zpKtrKdlDMqgREuNf8ixXSwMFTNj5wltKyETaLy0K9tbgvSAj9mYcCErFu4CFedX5HiznN3ttOkU8y0hr3oR3tTTLZY0oes88ibvLtF5qqTHx6smX7HoTlq66F5+4fIvXmKGii9LE80rLl0azInQIawNHbC5/YvBBy9muBJK+d3NR5tT72chrdAGmAx6P5cazKdQduqDMQpapew09sDPhPbbGVEurXRHXKjsYdDrqkuWHYzLXFk1BSwp/0bNctG9OTUg9hHEn1/Ij656fqqp9DLHBx87ECNGABCwwwLul4m1h+PaI69YXLpwP6v8DOOLaH22GPDIchgrP";

    private TFObjectDetector TFOD; // TensorFlow Object Detections
    private VuforiaLocalizer Vuforia; // Used by TFOD to interact with cameras

    @Override
    public void runOpMode() {
        // SETUP //
        initVuforia();
        initTFOD();

        if (TFOD != null) {
            TFOD.activate();
            TFOD.setZoom(2.5, 16.0 / 9.0);
        }

        // GET MOTORS //
        // control hub
        frontLeft = hardwareMap.dcMotor.get("leftFront");
        frontRight = hardwareMap.dcMotor.get("rightFront");
        backLeft = hardwareMap.dcMotor.get("leftRear");
        backRight = hardwareMap.dcMotor.get("rightRear");

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setMode(RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(RunMode.STOP_AND_RESET_ENCODER);

        // START //
        telemetry.addData("Say", "Hello, World!");
        telemetry.update();
        waitForStart();

        drive(0.5, 10);
        strafe(0.5, 10);

        while (opModeIsActive()) {
            if (TFOD != null) { // if out TFOD exists, see if we recognize anything new
                List<Recognition> updatedRecognitions = TFOD.getUpdatedRecognitions();
                if (updatedRecognitions != null) { // if there are new recognitions:
                    telemetry.addData("# Object Detected", updatedRecognitions.size());

                    // step through the list of recognitions and display boundary info
                    int i = 0;
                    for (Recognition recognition : updatedRecognitions) {
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                recognition.getLeft(), recognition.getTop());
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                recognition.getRight(), recognition.getBottom());
                        i++;
                    }
                    telemetry.update();
                }
            }
        }
    }

    public void drive(double power, double distance) {
        encoderDrive(power, power, power, power, distance);
    }

    public void strafe(double power, double distance) {
        encoderDrive(power, power, -power, -power, distance);
    }

    public void rotate(double power, double degree) {
        encoderDrive(power, -power, power, -power, degree); //TODO: find distance using degree
    }

    public void encoderDrive(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower, double distance) {
        if (opModeIsActive()) {
            double rotationsNeeded = distance / WHEEL_CIRCUMFERENCE;
            int encoderDrivingTarget = (int) (rotationsNeeded * MOTOR_TICK_COUNT);
            int leftTarget = encoderDrivingTarget + frontLeft.getCurrentPosition();
            int rightTarget = encoderDrivingTarget + frontRight.getCurrentPosition();

            // goals
            frontLeft.setTargetPosition(leftTarget);
            frontRight.setTargetPosition(rightTarget);
            backLeft.setTargetPosition(leftTarget);
            backRight.setTargetPosition(rightTarget);

            // start!
            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);

            frontLeft.setMode(RunMode.RUN_TO_POSITION);
            frontRight.setMode(RunMode.RUN_TO_POSITION);
            backLeft.setMode(RunMode.RUN_TO_POSITION);
            backRight.setMode(RunMode.RUN_TO_POSITION);

            // while the robot is active, time out not activated, and both motors are running
            while (opModeIsActive() && frontLeft.isBusy() && frontLeft.isBusy()) {
                telemetry.addData("Path", "Running at %7d :%7d",
                        frontLeft.getCurrentPosition(),
                        frontRight.getCurrentPosition());
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

    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        Vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    private void initTFOD() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        TFOD = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, Vuforia);
        TFOD.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}

// 14413!

// /\__/\
// (=o.o=)
// |/--\|
// (")-(")
