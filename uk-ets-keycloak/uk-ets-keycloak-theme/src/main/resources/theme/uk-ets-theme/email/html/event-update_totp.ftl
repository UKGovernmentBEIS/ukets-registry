<html>
<body>
${kcSanitize(msg("eventUpdateTotpBodyHtml1",event.date, event.ipAddress))?no_esc} <a href="mailto: ${msg("etrAddress")}">${msg("eventUpdateTotpBodyHtml2")} </a>.
<p>${msg("eventUpdateTotpBodyHtml3")}</p>
</body>
</html>
