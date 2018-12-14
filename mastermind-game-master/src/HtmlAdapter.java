
/**
 * Responsible to generate the html page that will be shown to the user.
 */
public class HtmlAdapter implements IHttpAdapter {

    /**
     * Generates the final html (header and body) as a simple string to
     * be returned to the client from a game state.
     * @param game the mastermind game object
     * @return the html as a single string
     */
    public String generateStringFromMastermind(Mastermind game) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html>");
        html.append("<html lang=\"en\" style=\"height:100%;\">");
        html.append(generateHtmlHeaderFile());
        html.append(generateHtmlBody(game));
        html.append("</html>");

        return html.toString();
    }

    /**
     * Generates the html page header
     * @return html header as a string
     */
    private String generateHtmlHeaderFile() {
        StringBuilder header = new StringBuilder();

        header.append("<head>");
            header.append("<meta charset=\"utf-8\">");
            header.append("<title>The Mastermind Game</title>");
            header.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\" integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">");
            header.append("<link rel=\"icon\" href=\"https://apprecs.org/ios/images/app-icons/256/79/318226297.jpg\">");

            header.append(generateHtmlJavaScript(Mastermind.GAME_COLORS));
            header.append(generateHtmlCss(Mastermind.GAME_COLORS));
        header.append("</head>");

        return header.toString();
    }

    /**
     * Generates the javascript for the html page
     * @param gameColors string array containing color names
     * @return the html javascript as a single string
     */
    private String generateHtmlJavaScript(String gameColors[]) {
        StringBuilder script = new StringBuilder();
        script.append("<noscript><style> .jsonly { display: none } </style></noscript>");

        script.append("<script>");

            script.append("var colors = [");
            String array = "";
            for (String color : gameColors) {
                array += "\"" + color + "\", ";
            }
            array = array.substring(0, array.length() - 2);
            array += "];";
            script.append(array);
            script.append("var color1 = 0;");
            script.append("var color2 = 0;");
            script.append("var color3 = 0;");
            script.append("var color4 = 0;");
            script.append("var rounds = 1;");

            script.append("function chooseColor(element, colorIndex) {");
                script.append("$(element).removeClass(colors[window[colorIndex]]);");
                script.append("++window[colorIndex];");
                script.append("if (window[colorIndex] === colors.length)");
                    script.append("window[colorIndex] = 0;");
                script.append("$(element).addClass(colors[window[colorIndex]]);");
            script.append("}");

            script.append("function sendColor() {");
                script.append("var ajaxRequest = new XMLHttpRequest();");
                script.append("ajaxRequest.onreadystatechange = function() {");
                    script.append("if (this.readyState == 4 && this.status == 200) {");
                        script.append("var res = this.responseText.split('_');");
                        script.append("rounds = res[0];");
                        script.append("var goodPos = res[1];");
                        script.append("var goodCol = res[2];");
                        script.append("var overallIndex = 0;");

                        script.append("$( \"#circle_\" + rounds + \"_1\").removeClass('gray').addClass(colors[color1]);");
                        script.append("$( \"#circle_\" + rounds + \"_2\").removeClass('gray').addClass(colors[color2]);");
                        script.append("$( \"#circle_\" + rounds + \"_3\").removeClass('gray').addClass(colors[color3]);");
                        script.append("$( \"#circle_\" + rounds + \"_4\").removeClass('gray').addClass(colors[color4]);");

                        script.append("if (goodPos == 4) {");
                            script.append("$(\"#submit_javascript\").addClass(\"disabled\");");
                            script.append("$(\"#submit_javascript\").prop(\"disabled\",true);");
                            script.append("$(\"#winLabel\").prop(\"hidden\",false);");
                            script.append("$(\"#newGame_javascript\").prop(\"hidden\",false);");
                        script.append("}");
                        script.append("else if (rounds == 12) {");
                            script.append("$(\"#submit_javascript\").addClass(\"disabled\");");
                            script.append("$(\"#submit_javascript\").prop(\"disabled\",true);");
                            script.append("$(\"#lostLabel\").prop(\"hidden\",false);");
                            script.append("$(\"#newGame_javascript\").prop(\"hidden\",false);");
                        script.append("}");

                        script.append("while (goodPos != 0) {");
                            script.append("goodPos--;");
                            script.append("overallIndex++;");
                            script.append("$( \"#mcircle_\" + rounds + \"_\" + overallIndex).removeClass('gray').addClass('red');");
                        script.append("}");
                        script.append("while (goodCol != 0) {");
                            script.append("goodCol--;");
                            script.append("overallIndex++;");
                            script.append("$( \"#mcircle_\" + rounds + \"_\" + overallIndex).removeClass('gray').addClass('white');");
                        script.append("}");

                    script.append("}");
                script.append("};");

                script.append("ajaxRequest.open(\"GET\", \"/play.html?color1=\" + colors[color1] + \"&color2=\" + colors[color2] + \"&color3=\" + colors[color3] + \"&color4=\" + colors[color4], true);");
                script.append("ajaxRequest.send();");
            script.append("}");

            script.append("function startNewGame() {");
                script.append("location.reload();");
            script.append("}");

        script.append("</script>");

        return script.toString();
    }

    /**
     * Generates the css for the html page
     * @param gameColors string array containing color names
     * @return the css for the page as a single string
     */
    private String generateHtmlCss(String gameColors[]) {
        StringBuilder style = new StringBuilder();
        style.append("<style>");
        style.append(".circle {");
            style.append("-moz-border-radius: 50px/50px;");
            style.append("-webkit-border-radius: 50px 50px;");
            style.append("border-radius: 50px/50px;");
            style.append("border: solid 2px #000000;");
            style.append("width: 25px;");
            style.append("height: 25px;");
        style.append("}");
        style.append(".small-circle {");
            style.append("-moz-border-radius: 50px/50px;");
            style.append("-webkit-border-radius: 50px 50px;");
            style.append("border-radius: 50px/50px;");
            style.append("border: solid 1px #000000;");
            style.append("width: 10px;");
            style.append("height: 10px;");
            style.append("margin-top: 7px;");
        style.append("}");

        for (String color : gameColors) {
            style.append("." + color + "{");
            style.append("background: " + color + ";");
            style.append("}");
        }

        style.append(".gray {");
            style.append("background: gray;");
        style.append("}");
        style.append(".row-circle {");
            style.append("height:30px;");
        style.append("}");
        style.append("</style>");

        return style.toString();
    }

    /**
     * Generates the whole html body. Takes the informations from the game to adapt the colors and fields
     * @param game the mastermind game
     * @return the html body as a single string
     */
    private String generateHtmlBody(Mastermind game) {
        StringBuilder body = new StringBuilder();

        body.append("<body style=\"background-color:beige;height:100%\">");
            body.append("<div class=\"container-fluid h-100\">");
                body.append("<div class=\"row h-100\">");
                    body.append("<div class=\"col-md-2\"></div>");
                    body.append("<div class=\"col-md-8\" style=\"background-color:bisque;\">");
                         body.append("<div class=\"row justify-content-center\">");
                            body.append("<div class=\"col-md-12 text-center\">");
                                body.append("<h1 class=\"display-4\">The Mastermind Game !</h1>");
                                body.append("<hr>");
                            body.append("</div>");
                        body.append("</div>");
                        body.append("<div class=\"row\">");


                            body.append(generateHtmlBodyRows(game.roundValues));
                            body.append(generateHtmlBodyColorSelection(Mastermind.GAME_COLORS, game));

                            body.append("</div>");
                        body.append("</div>");

                        body.append("</div>");
                        body.append("<div class=\"col-md-2\"></div>");
                body.append("</div>");
            body.append("</div>");


        body.append(generateHtmlBodyScript());
        body.append("</body>");

        return body.toString();
    }

    /**
     * Generates the body rows, which are the color circles in the left part of the UI.
     * @param rowsValues the colors values of the current game
     * @return the body rows as a single string
     */
    private String generateHtmlBodyRows(String rowsValues[]) {
        StringBuilder rows = new StringBuilder();
        rows.append("<div class=\"col-md-8\">");

        for (int i = 0; i < Mastermind.MAXIMUM_NUMBER_OF_TRY ; i++) {
            rows.append("<div class=\"row row-circle\">");
            rows.append("<div class=\"col-md-3\"></div>");

            if (rowsValues[i] == null || rowsValues[i].equals("")) {
                rows.append("<div class=\"col-md-1\"><div id=\"circle_" + (i+1) + "_1\" class=\"circle gray\"></div></div>");
                rows.append("<div class=\"col-md-1\"><div id=\"circle_" + (i+1) + "_2\" class=\"circle gray\"></div></div>");
                rows.append("<div class=\"col-md-1\"><div id=\"circle_" + (i+1) + "_3\" class=\"circle gray\"></div></div>");
                rows.append("<div class=\"col-md-1\"><div id=\"circle_" + (i+1) + "_4\" class=\"circle gray\"></div></div>");
                rows.append("<div class=\"col-md-1\"><div id=\"mcircle_" + (i+1) + "_1\" class=\"small-circle gray\"></div></div>");
                rows.append("<div class=\"col-md-1\"><div id=\"mcircle_" + (i+1) + "_2\" class=\"small-circle gray\"></div></div>");
                rows.append("<div class=\"col-md-1\"><div id=\"mcircle_" + (i+1) + "_3\" class=\"small-circle gray\"></div></div>");
                rows.append("<div class=\"col-md-1\"><div id=\"mcircle_" + (i+1) + "_4\" class=\"small-circle gray\"></div></div>");
            } else {
                rows.append("<div class=\"col-md-1\"><div id=\"circle_" + (i+1) + "_1\" class=\"circle " + Mastermind.GAME_COLORS[Integer.parseInt(rowsValues[i].substring(0, 1))] + "\"></div></div>");
                rows.append("<div class=\"col-md-1\"><div id=\"circle_" + (i+1) + "_2\" class=\"circle " + Mastermind.GAME_COLORS[Integer.parseInt(rowsValues[i].substring(1, 2))] + "\"></div></div>");
                rows.append("<div class=\"col-md-1\"><div id=\"circle_" + (i+1) + "_3\" class=\"circle " + Mastermind.GAME_COLORS[Integer.parseInt(rowsValues[i].substring(2, 3))] + "\"></div></div>");
                rows.append("<div class=\"col-md-1\"><div id=\"circle_" + (i+1) + "_4\" class=\"circle " + Mastermind.GAME_COLORS[Integer.parseInt(rowsValues[i].substring(3, 4))] + "\"></div></div>");

                int overallIndex = 0;
                for (int j = 0 ; j < Integer.parseInt(rowsValues[i].substring(4, 5)) ; j++) {
                    rows.append("<div class=\"col-md-1\"><div id=\"mcircle_" + (i + 1) + "_" + (overallIndex+1) + "\" class=\"small-circle red\"></div></div>");
                    overallIndex++;
                }
                for (int j = 0 ; j < Integer.parseInt(rowsValues[i].substring(5, 6)) ; j++) {
                    rows.append("<div class=\"col-md-1\"><div id=\"mcircle_" + (i + 1) + "_" + (overallIndex+1) + "\" class=\"small-circle white\"></div></div>");
                    overallIndex++;
                }
                for (int j = Integer.parseInt(rowsValues[i].substring(4, 5)) + Integer.parseInt(rowsValues[i].substring(5, 6)) ; j < 4 ; j++) {
                    rows.append("<div class=\"col-md-1\"><div id=\"mcircle_" + (i + 1) + "_" + (overallIndex+1) + "\" class=\"small-circle gray\"></div></div>");
                    overallIndex++;
                }
            }
            rows.append("</div>");
        }

        rows.append("</div>");
        return rows.toString();
    }

    /**
     * Generates the color buttons and the text located in the right side of the UI.
     * @param colors string array containing color names
     * @param game the mastermind game
     * @return the body color selection html part in a single string
     */
    private String generateHtmlBodyColorSelection(String colors[], Mastermind game) {
        StringBuilder selection = new StringBuilder();

        selection.append("<div class=\"col-md-4\">");
            selection.append("<span class=\"jsonly\">");
                selection.append("<h4>Your next guest ...</h4>");
                selection.append("<div class=\"row\">");
                    selection.append("<div class=\"col-md-1\"><div class=\"circle red\" onclick=\"chooseColor(this, 'color1');\"></div></div>");
                    selection.append("<div class=\"col-md-1\"><div class=\"circle red\" onclick=\"chooseColor(this, 'color2');\"></div></div>");
                    selection.append("<div class=\"col-md-1\"><div class=\"circle red\" onclick=\"chooseColor(this, 'color3');\"></div></div>");
                    selection.append("<div class=\"col-md-1\"><div class=\"circle red\" onclick=\"chooseColor(this, 'color4');\"></div></div>");
                selection.append("</div>");
                selection.append("<br>");
                selection.append("<button type=\"button\" id=\"submit_javascript\" class=\"btn btn-primary\" onclick=\"sendColor();\">Submit</button>");
                selection.append("<br>");
                selection.append("<hr>");
                selection.append("<h4 id=\"winLabel\" hidden=\"true\">Congratulations, you won the game !</h4>");
                selection.append("<h4 id=\"lostLabel\" hidden=\"true\">Unfortunately, you lost the game. Better luck next time !</h4>");
                selection.append("<button id=\"newGame_javascript\"type=\"button\" hidden=\"true\" class=\"btn btn-success\" onclick=\"startNewGame();\">");
                    selection.append("Start a new game");
                selection.append("</button>");
                selection.append("<br>");
                selection.append("</span>");
            selection.append("<noscript>");
                selection.append("<form  method=\"post\">");
                    selection.append("<h4>Your next guest ...</h4>");
                    selection.append("<label for=\"color1\"> Color 1 :&nbsp;</label>");
                    selection.append("<select name=\"color1\" id=\"color1\">");
                        for (String color : colors) {
                            selection.append("<option value=\"" + color + "\">" + color + "</option>");
                        }
                    selection.append("</select>");
                    selection.append("<br>");
                    selection.append("<label for=\"color2\"> Color 2 :&nbsp;</label>");
                    selection.append("<select name=\"color2\" id=\"color2\">");
                        for (String color : colors) {
                            selection.append("<option value=\"" + color + "\">" + color + "</option>");
                        }
                    selection.append("</select>");
                    selection.append("<br>");
                    selection.append("<label for=\"color3\"> Color 3 :&nbsp;</label>");
                    selection.append("<select name=\"color3\" id=\"color3\">");
                        for (String color : colors) {
                            selection.append("<option value=\"" + color + "\">" + color + "</option>");
                        }
                    selection.append("</select>");
                    selection.append("<br>");
                    selection.append("<label for=\"color4\"> Color 4 :&nbsp;</label>");
                    selection.append("<select name=\"color4\" id=\"color4\">");
                        for (String color : colors) {
                            selection.append("<option value=\"" + color + "\">" + color + "</option>");
                        }
                    selection.append("</select>");
                    selection.append("<br>");
                    selection.append("<button type=\"submit\" class=\"btn btn-primary\">Submit</button>");
                selection.append("</form>");
                if (game.isGameOver) {
                    selection.append("<hr>");
                    selection.append("<div>");
                    if (game.nbOfTry == Mastermind.MAXIMUM_NUMBER_OF_TRY) {
                        selection.append("<h4 id=\"lostLabel\">Unfortunately, you lost the game. Better luck next time !</h4>");
                    } else {
                        // Victory
                        selection.append("<h4 id=\"winLabel\">Congratulations, you won the game !</h4>");

                    }
                        selection.append("<p><i>(refresh page to start a new game)<i></p>");
                    selection.append("<div>");
                }
            selection.append("</noscript>");
        selection.append("</div>");


        return selection.toString();
    }

    /**
     * Generate the html body scripts (for libraries usage)
     * @return the html body script
     */
    private String generateHtmlBodyScript() {
        StringBuilder script = new StringBuilder();

        script.append("<script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\" crossorigin=\"anonymous\"></script>");
        script.append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" integrity=\"sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q\" crossorigin=\"anonymous\"></script>");
        script.append("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\" integrity=\"sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl\" crossorigin=\"anonymous\"></script>");

        return script.toString();
    }
}
