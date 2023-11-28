-- first delete all compliance notifications because if the user updates the existing ones,
-- the new body of the notifications will be replaced by the old one:

DELETE
FROM notification
where notification_definition_id in (
    select id
    from notification_definition
    where type = 'YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS'
);

UPDATE public.notification_definition
SET short_text = 'The UK Emissions Trading Scheme (UK ETS): A Guide to the entry of emissions and surrender in the UK ETS Registry ',
    long_text  = '<p><strong>Account Holder: ${accountHolder.name}</strong></p><p><strong>Account ID: ${accountId}</strong></p><p><br></p><p>Dear ${user.firstName} ${user.lastName},      </p><p><br></p><p>This Notification is being sent to you by the Registry Administrator in your capacity as an Authorised Representative of ${accountId}.  You have been sent this notification because the Account Holder of this Registry Account has UK ETS reporting and surrender obligations for 2021.</p><p>More information can be found in the guidance for the surrender of allowances. To access the guidance:</p><p><br></p><ol><li>Sign in to the Registry</li><li>Go to the bottom of the page and select “Guidance”</li><li>Select “Help with Emissions and Surrenders”</li></ol><p><br></p><p>Please consult this guide and follow the steps carefully. </p><p><br></p><p>The reporting and surrender deadlines for the 2021 scheme year are as follows:</p><p><br></p><ul><li>Submission of report of verified emissions to the Regulator:	<strong>31st March 2022</strong></li><li>Surrender of allowances: <strong>30th April 2022</strong></li></ul><p><br></p><p>Your Regulator and the Registry Administrator will ensure that the verified emissions figure in your report is entered into your account in the UK ETS Registry as soon as possible.</p><p><br></p><p>The 15th and 18th of April 2022 are non-working days. The UK Registry Helpdesk will not be available to assist you on these dates.</p><p><br></p><p>Kind Regards, </p><p>The UK Registry Team</p><p><br></p><p><strong>UK Registry Team:</strong> Climate Change, Trading &amp; Regulatory Services</p><p><strong>Environment Agency: </strong>Richard Fairclough House, Knutsford Road, Warrington, Cheshire, WA4 1HT</p>'
WHERE type = 'YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS';
