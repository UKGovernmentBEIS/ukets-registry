<html>
<body>
${kcSanitize(msg("eventUpdatePasswordBodyHtml1",event.date, event.ipAddress))?no_esc} <a href="mailto: ${msg("etrAddress")}">${msg("eventUpdatePasswordBodyHtml2")} </a>.
<p>${msg("eventUpdatePasswordBodyHtml3")}</p>
</body>
</html>
