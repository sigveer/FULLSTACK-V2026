package com.iksystem.common.resend

import org.springframework.stereotype.Component

@Component
class EmailTemplateBuilder {
    private fun baseLayout(content: String): String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8"/>
        </head>
        <body style="margin:0;padding:0;background:#F3F2EF;font-family:Arial,sans-serif;">
          <div style="max-width:600px;margin:40px auto;background:white;border-radius:16px;padding:32px;">
            $content
          </div>
        </body>
        </html>
        """.trimIndent()
    }

    fun registrationEmail(
        title: String,
        description: String,
    ): String {
        val content = """
            <h2 style="margin-top:0;color:#111;">$title</h2>
            <p style="color:#555;font-size:15px;">$description</p>
        """

        return baseLayout(content)
    }

    fun actionEmail(
        title: String,
        description: String,
        buttonText: String,
        buttonUrl: String,
        statusColor: String = "#5B4FDB"
    ): String {

        val content = """
            <h2 style="margin-top:0;color:#111;">$title</h2>
            <p style="color:#555;font-size:15px;">$description</p>

            <div style="margin:32px 0;">
                <a href="$buttonUrl"
                   style="
                   background:$statusColor;
                   color:white;
                   padding:14px 24px;
                   border-radius:10px;
                   text-decoration:none;
                   font-weight:600;
                   display:inline-block;">
                   $buttonText
                </a>
            </div>

            <p style="font-size:12px;color:#888;">
                If the button does not work, copy this link:<br/>
                $buttonUrl
            </p>
        """

        return baseLayout(content)
    }

    fun statusCard(
        title: String,
        rows: List<Pair<String, String>>,
        statusLabel: String,
        statusColor: String
    ): String {

        val rowsHtml = rows.joinToString("") {
            """
            <tr>
              <td style="padding:8px 0;color:#666;">${it.first}</td>
              <td style="padding:8px 0;text-align:right;font-weight:600;">${it.second}</td>
            </tr>
            """
        }

        val content = """
            <h2 style="margin-top:0;">$title</h2>

            <table width="100%" style="margin-top:16px;">
              $rowsHtml
            </table>

            <div style="margin-top:24px;">
              <span style="
                  background:$statusColor;
                  color:white;
                  padding:8px 14px;
                  border-radius:20px;
                  font-size:12px;
                  font-weight:600;">
                $statusLabel
              </span>
            </div>
        """

        return baseLayout(content)
    }
}