function responseTimeFieldFormatter(value, row) {
    if (value === -1) {
        return "Non disponible";
    }
    return value;
}

function statusFormatter(value) {
    // need to support boolean and strings for retro-compat
    if (typeof value === 'undefined' || value === "") {
        return "inconnu";
    } else if (value === "true" || (typeof value === 'boolean' && value === true)) {
        return "valide";
    } else if (value === "false" || (typeof value === 'boolean' && value === false)) {
        return "invalide";
    }
    return value;
}
function validationStatusFormatter(value) {
    return statusFormatter(value);
}
function validationFormatter(validating) {
    if (validating) {
        return "Validation active ";
    } else {
        return "Validation inactive ";
    }
}

function blockingFormatter(blocking) {
    if (blocking) {
        return "Mode bloquant";
    } else {
        return "Mode Non bloquant";
    }
}
