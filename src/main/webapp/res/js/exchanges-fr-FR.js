function responseTimeFieldFormatter(value, row) {
    if (value === -1) {
        return "Non disponible";
    }
    return value;
}
function statusFormatter(value) {
    if (value === "") {
        return "inconnu";
    } else if (value === "true") {
        return "valide";
    } else if (value === "false") {
        return "invalide";
    }
    return value;
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
