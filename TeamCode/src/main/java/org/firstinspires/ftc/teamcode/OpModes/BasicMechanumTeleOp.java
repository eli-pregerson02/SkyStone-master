package org.firstinspires.ftc.teamcode.OpModes;

        import com.qualcomm.hardware.bosch.BNO055IMU;
        import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
        import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
        import com.qualcomm.robotcore.hardware.CRServo;
        import com.qualcomm.robotcore.hardware.DcMotor;
        import com.qualcomm.robotcore.hardware.Servo;
        import org.firstinspires.ftc.teamcode.Hardware.Arm;
        import org.firstinspires.ftc.teamcode.math.Vector2d;


@TeleOp(name = "Mechanum")public class BasicMechanumTeleOp extends LinearOpMode {
    public void runOpMode() {
        //Variables


        boolean rightArmB = true;
        boolean BaseGrabberDebounce = false;


        DcMotor fl = hardwareMap.dcMotor.get("front_left_motor");
        DcMotor fr = hardwareMap.dcMotor.get("front_right_motor");
        DcMotor bl = hardwareMap.dcMotor.get("back_left_motor");
        DcMotor br = hardwareMap.dcMotor.get("back_right_motor");
        DcMotor clawMotor = hardwareMap.dcMotor.get("claw_motor");

        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters param = new BNO055IMU.Parameters();


        clawMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        clawMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        CRServo handServo = hardwareMap.crservo.get("hand_servo");
        Servo buildPlateServo = hardwareMap.servo.get("BuildPlate_servo");
        Servo buildPlateServo2 = hardwareMap.servo.get("Build_Plate_servo");



        //Motors
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);
        fl.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.FORWARD);

        // Gyro init

        param.loggingEnabled = false;
        param.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        param.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu.initialize(param);


        waitForStart();
        Arm intake = new Arm();

        while (opModeIsActive()) {



            //hand control
            if (gamepad1.left_bumper) {
                handServo.setPower(0.3);
            } else if (gamepad1.right_bumper) {
                handServo.setPower(-0.3);
            }

            //arm control
            if (gamepad1.y) {
                //position one
                intake.move(1);
            } else if (gamepad1.b) {
                //postition two
                intake.move(2);

            } else if (gamepad1.a) {
                //position three
                intake.move(3);
            }

            //motor setting for drivetrain
            Vector2d input = new Vector2d(gamepad1.left_stick_y / 2, gamepad1.left_stick_x / 2);
            double rot = gamepad1.right_trigger - gamepad1.left_trigger;
            fl.setPower(input.x + input.y + rot);
            fr.setPower(input.x - input.y - rot);
            bl.setPower(input.x - input.y + rot);
            br.setPower(input.x + input.y - rot);

            //motor setting for arm
            double armSpeed = intake.move(clawMotor.getCurrentPosition());
            clawMotor.setPower(armSpeed);

            //toggles Build Plate Grabbers
            if (gamepad1.x && !BaseGrabberDebounce) {
                rightArmB = !rightArmB;
                BaseGrabberDebounce = true;

            } else if (!gamepad1.x) {
                BaseGrabberDebounce = false;
            }

            if (rightArmB) {
                buildPlateServo.setPosition(1);
                buildPlateServo2.setPosition(1);

            } else {
                buildPlateServo.setPosition(0.5);
                buildPlateServo2.setPosition(0.5);
            }


            telemetry.addData("frPower: ", fr.getPower());
            telemetry.addData("flPower: ", fl.getPower());
            telemetry.addData("brPower: ", br.getPower());
            telemetry.addData("blPower: ", bl.getPower());
            telemetry.addData("Claw Encoder: ", clawMotor.getCurrentPosition());

            telemetry.update();


        }
    }
}
