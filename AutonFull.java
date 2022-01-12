/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

/**
 * This 2020-2021 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the Freight Frenzy game elements.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@TeleOp(name = "AutonFull", group = "Auton")
public class AutonFull extends LinearOpMode {
DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backLeftMotor;
    DcMotor backRightMotor;
    private ElapsedTime runtime = new ElapsedTime();



    /* Note: This sample uses the all-objects Tensor Flow model (FreightFrenzy_BCDM.tflite), which contains
   * the following 4 detectable objects
   *  0: Ball,
   *  1: Cube,
   *  2: Duck,
   *  3: Marker (duck location tape marker)
   *
   *  Two additional model assets are available which only contain a subset of the objects:
   *  FreightFrenzy_BC.tflite  0: Ball,  1: Cube
   *  FreightFrenzy_DM.tflite  0: Duck,  1: Marker
   */
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
      "Ball",
      "Cube",
      "Duck",
      "Marker"
    };

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            "ASFtnEj/////AAABmTdt18pG5U+iioqvGlhBis+Itb3ank1zpKtrKdlDMqgREuNf8ixXSwMFTNj5wltKyETaLy0K9tbgvSAj9mYcCErFu4CFedX5HiznN3ttOkU8y0hr3oR3tTTLZY0oes88ibvLtF5qqTHx6smX7HoTlq66F5+4fIvXmKGii9LE80rLl0azInQIawNHbC5/YvBBy9muBJK+d3NR5tT72chrdAGmAx6P5cazKdQduqDMQpapew09sDPhPbbGVEurXRHXKjsYdDrqkuWHYzLXFk1BSwp/0bNctG9OTUg9hHEn1/Ij656fqqp9DLHBx87ECNGABCwwwLul4m1h+PaI69YXLpwP6v8DOOLaH22GPDIchgrP";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {

        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(2.5, 16.0/9.0);
        }
        // MOTORS AND SERVOS //
        // control hub
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeft");
        frontRightMotor = hardwareMap.dcMotor.get("frontRight");
        backLeftMotor = hardwareMap.dcMotor.get("backLeft");
         backRightMotor = hardwareMap.dcMotor.get("backRight");

        // expansion hub
        DcMotor linearSlide = hardwareMap.dcMotor.get("linearSlide");
        DcMotor intakeSystem = hardwareMap.dcMotor.get("intakeSystem");
        DcMotor duckWheel = hardwareMap.dcMotor.get("duckSystem");

        // servo
        Servo deliverySystem = hardwareMap.servo.get("deliverySystem");

        // CONFIGURATION //
        // motor power config
        double calmDownMecanum = 1; // how much the chassis just needs to chill
        double intakeSystemRate = 1; // how fast the intake system should run
        double duckWheelRate = 0.5; //how fast the duck wheel could spin
        double linearSlideLevelRate = 1; // calibrate to reach level 1, 2, 3
        double linearSlideBaseRate = 1; // how fast the linear slide goes up when holding down button

        // delivery system servo config
        double initialPosition = 0.7; // position when intake system running
        double movingPosition = 0.5; // position when holding piece and going up
        double droppingPosition = 0.3; // position when dropping piece

        // SETUP //
       frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE); // backwards gear, much love ryman <3
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);


        // rising edge button detector setup
        boolean risingA = false;
        boolean risingX = false;
        boolean risingY = false;
        boolean risingLeftBumper = false;
        boolean risingRightBumper = false;


        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

DriveForward(.25);
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                      telemetry.addData("# Object Detected", updatedRecognitions.size());
                      // step through the list of recognitions and display boundary info.
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
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
       tfodParameters.minResultConfidence = 0.8f;
       tfodParameters.isModelTensorFlow2 = true;
       tfodParameters.inputSize = 320;
       tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
       tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }


    public void driveForwardDistance(double power, int distance){
        if (opModeIsActive()) {
            telemetry.addData("Say", frontLeftMotor.getCurrentPosition());
            telemetry.update();

            frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
           // frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            //backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            //backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            frontLeftMotor.setTargetPosition(1000000);
           // frontRightMotor.setTargetPosition(1000000);
            //backLeftMotor.setTargetPosition(distance);
            //backRightMotor.setTargetPosition(distance);

            frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           // frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            //backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            //backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();

            DriveForward(power);

            while ((opModeIsActive() &&
                    (runtime.seconds() < 4) &&
                    frontLeftMotor.isBusy() &&
                    frontRightMotor.isBusy())) {

            }

            stopDriving();
            frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
           // backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
           // backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        }
    }

    public void DriveForward(double power){
        frontLeftMotor.setPower(power);
        backLeftMotor.setPower(power);
        frontRightMotor.setPower(power);
        backRightMotor.setPower(power);
    }
    public void stopDriving(){

        DriveForward(0);
    }
    public void strafeRight(double power){
        frontLeftMotor.setPower(power);
        backLeftMotor.setPower(power);
        frontRightMotor.setPower(power * -1);
        backRightMotor.setPower(power * -1);

    }

    public void strafeLeft(double power){
        frontLeftMotor.setPower(power * -1);
        backLeftMotor.setPower(power * -1);
        frontRightMotor.setPower(power);
        backRightMotor.setPower(power);

    }
}

