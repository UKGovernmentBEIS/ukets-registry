var context = $evaluation.getContext();
var identity = context.getIdentity();
var attributes = identity.getAttributes();
var state = attributes.getValue('state').asString(0);

if (state.toUpperCase()==='ENROLLED') {
    $evaluation.grant();
}