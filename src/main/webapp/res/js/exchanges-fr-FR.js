function responseTimeFieldFormatter(value, row) {
    if (value === -1) {
        return "Non disponible";
    }
    return value;
}


function statusFormatter(valid, xmlValid, validator) {
    if (valid) {
        return '<span class="text-success">valide</span>';
    }
    else if (!valid && validator && validator !== "") {
        return '<span class="text-danger">invalide</span>';
    }
    if (!xmlValid) {
        return '<span class="text-danger">invalide</span>';
    }
    return '<span class="text-warning">inconnu</span>';
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
