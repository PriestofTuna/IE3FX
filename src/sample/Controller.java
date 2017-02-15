package sample;

import com.sun.org.apache.bcel.internal.classfile.ExceptionTable;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.swing.*;

public class Controller {
    @FXML
    TextField text1;
    @FXML
    TextField text2;
    @FXML
    TextField text3;
    @FXML
    TextField text4;
    @FXML
    Label out1;
    @FXML
    Label out2;
    @FXML
    Label out3;
    @FXML
    Label out4;

    public String toBinary(String text) {
        try {
            if (text.equals("")) {
                return "";
            }
            String mantissa = "";
            //String binaryID[] = text1S.split(".");
            //int bI = Integer.parseInt(binaryID[0]);
            long bI = Long.parseLong(text);
            for (int i = 0; bI != 0; i++) {
                if (bI >= 1 || bI <= 1) {
                    mantissa = Math.abs(bI % 2) + mantissa;
                    bI = bI / 2;
                } else {
                    mantissa = "0" + mantissa;
                }
            }

            return mantissa;
        }catch(Exception e) {
            System.out.println("NaN or Infinity");
            //System.exit(0);
            return " NaN";
        }
    }
    public String mantissa23(String text) {
        for(int i = text.length()-1; i < 22; i++) {
            text+="0";
        }
        return text;
    }
    public void checkAll(ActionEvent actionEvent) {
        int mantissaLength[] = {23, 52};
        int Bias[] = {127, 1023};
        int expoBias[] = {8, 11};
        try {
            if (!text1.getText().isEmpty()) {
                String input = text1.getText();
                String sign = "0";
                String ints = "";
                String decs = "";
                String compiled[] = {"",""};


                if (text1.getText().equals("0")) {
                    //Needs to be able to represent both double and single precision 0
                    compiled[1] = ("0   00000000    000000000000000000000000");
                    compiled[0] = ("0   00000000000     0000000000000000000000000000000000000000000000000000");
                    out1.setText(compiled[1]);
                    out2.setText(compiled[0]);
                    //Double Pre
                } else if (text1.getText().equals("-0")) {
                    compiled[1] = ("1   00000000    000000000000000000000000");
                    compiled[0] = ("1   00000000000     0000000000000000000000000000000000000000000000000000");
                } else if (text1.getText().equalsIgnoreCase("NaN")) {
                    compiled[1] = ("0 11111111 val");
                    compiled[0] = ("0 11111111111 val");
                } else {
                    for (int i = 1; i > -1; i--) {
                        if (i == 0) {
                            System.out.println("32 bit rep" + "\n");
                        } else {
                            System.out.println("\n" + "64 bit rep");
                        }
                        if (input.contains("-")) {
                            sign = "1";
                            input = input.substring(1, input.length());
                        }
                        if (input.contains(".")) {
                            System.out.println("hits?");
                            int temp = input.indexOf(".");
                            Double d = new Double(input);
                            if (d.intValue() < 1) {
                                compiled[i] = (sign + "\t" + expo(Double.parseDouble(input), expoBias[i], mantissaLength[i], Bias[i]) + "\t" + toMantissa(deciConverter(mantissaLength[i], Double.parseDouble(input)), mantissaLength[i]));
                                System.out.println(compiled[i]);

                            } else if (d.intValue() > 1) {
                                String[] intDeci = input.split("\\.");
                                intDeci[1] = "." + intDeci[1];
                                System.out.println(intDeci[0] + "\t" + intDeci[1]);
                                compiled[i] = (sign + "\t" + expo(Double.parseDouble(input), expoBias[i], mantissaLength[i], Bias[i]) + "\t" + toMantissa(toBinary(intDeci[0]) + deciConverter(mantissaLength[i], Double.parseDouble(intDeci[1])), mantissaLength[i]));
                                System.out.println(compiled[i]);
                                System.out.println(expo(Double.parseDouble(input), expoBias[i], mantissaLength[i], Bias[i]));
                            } else {
                                String[] intDeci = input.split("\\.");
                                intDeci[1] = "." + intDeci[1];
                                String toB = expoLength(toBinary("" + Bias[i]), expoBias[i]);
                                System.out.println(intDeci[0] + "\t" + intDeci[1]);
                                compiled[i] =sign + "\t" + toB + "\t" + toMantissa(toBinary(intDeci[0]) + deciConverter(mantissaLength[i], Double.parseDouble(intDeci[1])), mantissaLength[i]);
                                System.out.println(compiled[i]);

                                //System.out.println(expo(Double.parseDouble(input), expoBias[i], mantissaLength[i], Bias[i]));
                            }
                        } else {
                            System.out.println("else: " + input + "\t" + input);
                            compiled[i] = sign + "\t" + expo(Double.parseDouble(input),expoBias[i], mantissaLength[i], Bias[i] ) + "\t" + toMantissa(toBinary((input)), mantissaLength[i]);
                            System.out.println(compiled[i]);
                        }
                    }
                    if(compiled[1].equals("1\t11111111\t00000000000000000000000") || compiled[1].equals("0\t11111111\t00000000000000000000000")) {
                        compiled[1] = "Infinity";
                    } else if(compiled[0].contains("1\t11111111\t")) {
                        compiled[1] = "NaN";
                    }
                    if(compiled[0].equals("1\t11111111111\t0000000000000000000000000000000000000000000000000000") || compiled[0].equals("1\t11111111111\t0000000000000000000000000000000000000000000000000000")) {
                        compiled[0] = "Infinity";
                    } else if(compiled[0].contains("1\t11111111111\t")) {
                        compiled[0] = "NaN";
                    }
                    out1.setText(compiled[0]);
                    out2.setText(compiled[1]);
                }
                //System.out.println(deciConverter(23, Double.parseDouble(text1.getText())));
                //System.out.println("To Mantissa " + toMantissa(deciConverter(23, Double.parseDouble(text1.getText())), 23));
                //System.out.println("To Merge " + merge(deciConverter(23, Double.parseDouble(text1.getText())), toBinary(input), 23));

                //0011001100110011001100110
                //00110011001100110011001
            }
            //System.out.println(toMantissa(toBinary("19") + deciConverter(23, .5),23));
            //System.out.println(expo(.5,8,23,127));
            if (!text2.getText().isEmpty()) {
                if((text3.getText().trim().length() != 8) && text3.getText().trim().length()!=11) {
                    //Remaining steps:
                    //1: getDecimal testing and implementation.
                    //2: special cases for 0, NaN, and infinity.
                    //3: error and all tests
                    //4: make whole numbers work Checku
                    //4: Make it look pretty. Nah fam
                    //5: do final tests for reasonable accuracy. Checku
                    //6: make Double precision run first checku
                    out3.setText("Not a Exponent");
                } else{
                    if((text4.getText().trim().equals("0000000000000000000000000000000000000000000000000000") && text3.getText().trim().equals("00000000000")) || (text4.getText().trim().equals("00000000000000000000000") && text3.getText().trim().equals("00000000"))) {
                        out4.setText("0");
                    }else if((text4.getText().trim().equals("0000000000000000000000000000000000000000000000000000") && text3.getText().trim().equals("11111111111")) || (text4.getText().trim().equals("00000000000000000000000") && text3.getText().trim().equals("00000000"))) {
                        out4.setText("Infinity");
                    } else if((!text4.getText().trim().equals("0000000000000000000000000000000000000000000000000000") && text3.getText().trim().equals("11111111111")) || (!text4.getText().trim().equals("00000000000000000000000") && text3.getText().trim().equals("11111111"))) {
                        out4.setText("NaN");
                    } else if((!text4.getText().trim().equals("0000000000000000000000000000000000000000000000000000") && text3.getText().trim().equals("00000000000")) || (!text4.getText().trim().equals("00000000000000000000000") && text3.getText().trim().equals("00000000"))) {
                        if(text4.getText().trim().length() == 23) {
                            out4.setText("Denormalized: " +getDecimal(mantissaConverterDe(text4.getText()), text3.getText(), text2.getText(), 127));
                        } else if(text4.getText().trim().length() == 52) {
                            out4.setText("Denormalized: " +getDecimal(mantissaConverterDe(text4.getText()),text3.getText(), text2.getText(), 1023));
                        } else {
                            out4.setText("Invalid Mantissa, required length 23, Your length: " + text4.getText().trim().length());
                        }
                    }
                    //for(int i = 0; i < 2; i++) {
                        //System.out.println("" + getDecimal(mantissaConverter("1" + "00011111000001100010010"),));
                    if(text4.getText().trim().length() == 23) {
                        out4.setText("" +getDecimal(mantissaConverter(text4.getText()), text3.getText(), text2.getText(), 127));
                    } else if(text4.getText().trim().length() == 52) {
                        out4.setText("" +getDecimal(mantissaConverter(text4.getText()),text3.getText(), text2.getText(), 1023));
                    } else {
                        out4.setText("Invalid Mantissa, required length 23, Your length: " + text4.getText().trim().length());
                    }
                }
                //System.out.println("" + getDecimal(mantissaConverter("1" + "00011111000001100010010"),));

            }
            if (!text3.getText().isEmpty()) {

            }
            if (!text4.getText().isEmpty()) {

            }
        }catch(Exception e) {
            if(e.getMessage().substring(0,1).contains("f")) {
                out1.setText("Out of Bounds number, NaN");
            }
            out1.setText(e.toString());
        }
    }

    public void text1A(KeyEvent keyEvent) {
        if(keyEvent.getCode()== KeyCode.ENTER) {

        }
    }
    public void text2A(KeyEvent keyEvent) {

    }
    public void text3A(KeyEvent keyEvent) {
    }
    public void text4A(KeyEvent keyEvent) {
        if(keyEvent.getCode()== KeyCode.ENTER) {
            //int expo = Integer.parseInt(text3.getText()) -127;
            double d = Double.parseDouble(text4.getText());
            System.out.println(d);
        }
    }
    public String deciConverter(int maxBits, double decimal) {
        double deciLoop =decimal;
        String mantissaDeci = "";
        double testval = 0;
        for(int i = -1; i >= -(maxBits) && deciLoop > 0; i--) {
            double pow2 = Math.pow(2,i);
            if(pow2 < deciLoop) {
                testval+=pow2;
                deciLoop-=pow2;
                mantissaDeci+="1";
            } else if(pow2 == deciLoop) {
                mantissaDeci+="1";
                testval+=pow2;
                System.out.println(testval + "\n" + mantissaDeci);
                return mantissaDeci;
            }else {
                mantissaDeci+=0;
            }
        }
        System.out.println(testval + "\n" + mantissaDeci);
        return mantissaDeci;
    }
    public String merge(String ints, String decs, int maxBits) {
        int mLength = (ints + decs).length();
        String merge =ints + decs;
//        if(mLength <=maxBits) {
//            return ints + decs;
//        } else {
//
//        }
        while(merge.length()>maxBits) {
            merge = merge.substring(0, merge.length()-1);
        }
        return merge;
    }
    public String toMantissa(String intPdec, int mantissaLength) {
        //System.out.println("No changes " + intPdec);
        String normalized = intPdec.substring(1,intPdec.length());
        System.out.println(normalized.length() +"   :" + mantissaLength + "\n" + normalized + "\n" + intPdec);
        while(normalized.length() > mantissaLength) {
            System.out.println(normalized.substring(0,normalized.length()-1));
            normalized= normalized.substring(0,normalized.length()-1);
        }
        while(normalized.length() < mantissaLength) {
            normalized+="0";
        }
        return normalized;
    }
    public String expo(Double val, int bitSize, int maxBits, int expoBias) {
        if(val < 1) {
            double tempval = val;
            int i = 0;
            while(tempval<1) {
                i--;
                tempval = val/Math.pow(2, i);
            }
            if(toBinary(expoBias+i +"").length()>Math.pow(2,bitSize)) {
                throw new NumberFormatException("NaN");
            }
            System.out.println(expoBias+i);
            String stringThing = toBinary(expoBias+i +"");
            if(stringThing.length() < bitSize) {
                int distance = bitSize-stringThing.length();
                while (distance > 0) {
                    distance--;
                    stringThing = "0" + stringThing;
                }
            }
            return stringThing;
        }else if(val >=2) {
            double tempval = val;
            int i = 0;
            do {
                i++;
                tempval = val/Math.pow(2, i);
                //System.out.println("\n"+ "Inside line 225 temp:" + (tempval + ": i: " + i) +"\n");
            } while(!(tempval<1));
            System.out.println(expoBias+i-1);
            String stringThing = toBinary((expoBias+i-1) +"");
            if(stringThing.length() < bitSize) {
                int distance = bitSize-stringThing.length();
                while (distance > 0) {
                    distance--;
                    stringThing = "0" + stringThing;
                }
            }
            return stringThing;
        } else {
            return ""+expoLength(toBinary(""+expoBias), bitSize);
        }
    }
    public String expoLength(String expo, int length) {
        while(expo.length() < length) {
            expo= "0" + expo;
        }
        return expo;
    }
    public double mantissaConverter(String mantissa) {
        double val = 1.0;
        if(mantissa.length()==23 || mantissa.length()==52) {
            for(int i = 1; i <= mantissa.length(); i++) {
                double tempVal =(Double.parseDouble(mantissa.substring(i-1,i)) * Math.pow(2, -i));
                val += (Double.parseDouble(mantissa.substring(i-1,i)) * Math.pow(2, -i));
                System.out.println(val + "\t" + mantissa.substring(i-1,i));
            }
        } else {
            out4.setText("Invalid Mantissa!");
        }
        return val;
    }
    public double mantissaConverterDe(String mantissa) {
        double val = 0.0;
        if(mantissa.length()==23 || mantissa.length()==52) {
            for(int i = 1; i <= mantissa.length(); i++) {
                double tempVal =(Double.parseDouble(mantissa.substring(i-1,i)) * Math.pow(2, -i));
                val += (Double.parseDouble(mantissa.substring(i-1,i)) * Math.pow(2, -i));
                System.out.println(val + "\t" + mantissa.substring(i-1,i));
            }
        } else {
            out4.setText("Invalid Mantissa!");
        }
        return val;
    }

    public double getDecimal(double val, String expo, String sign, int bias) {
        System.out.println(expo);
        double answer = 1.0;
        int exponent = toInteger(expo, bias);
        if(sign.equals("0")) {
            answer = 1.0;
        }else if(sign.equals("1")) {
            answer = -1.0;
        }
        return (answer * Math.pow(2,exponent) * val);
    }
    public int toInteger(String expo, int bias) {
        int answer = 0;
        int real = 0;
        int n = 0;
        for(int i = expo.length()-1; i >= 0; i--) {
            real +=(Integer.parseInt(expo.substring(i,i+1)) * Math.pow(2, n));
            System.out.println(real);
            answer+= Integer.parseInt(expo.substring(i,i+1)) * Math.pow(2, n);
            n++;
        }
        return answer - bias;
    }

}
