function responseTimeFieldFormatter(value, row) {
    if (value === -1) {
        return "Non disponible";
    }
    return value;
}

function statusFormatter(value) {
    // need to support boolean and strings for retro-compat
    if (typeof value === 'undefined' || value === "") {
        return '<span class="text-warning">inconnu</span>';
    } else if (value === "true" || (typeof value === 'boolean' && value === true)) {
        return '<span class="text-success">valide</span>';
    } else if (value === "false" || (typeof value === 'boolean' && value === false)) {
        return '<span class="text-danger">invalide</span>';
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
