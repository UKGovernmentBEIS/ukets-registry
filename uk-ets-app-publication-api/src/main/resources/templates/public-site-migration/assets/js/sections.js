function insertKpSections() {
  document.addEventListener("DOMContentLoaded", function () {
    for (let id = 1; id <= 4; id++) {
      document.getElementById("sections").innerHTML += load(
        `/kp-reports/section${id}/section${id}.html`
      );
    }
    appendOrdinal();
  });
}

function insertEtsSections() {
  document.addEventListener("DOMContentLoaded", function () {
    for (let id = 1; id <= 6; id++) {
      document.getElementById("sections").innerHTML += load(
        `/ets-reports/section${id}/section${id}.html`
      );
    }
    appendOrdinal();
    document.querySelectorAll('.show-more').forEach(button => {
      button.addEventListener('click', function () {
        const sectionId = this.id.split("-")[2];

        const hiddenRows = document.getElementById('hidden-rows-' + sectionId);
        hiddenRows.style.display = 'table-row-group';
        const visibleRows = document.getElementById('visible-rows-' + sectionId);
        visibleRows.style.display = 'none';

        this.style.display = 'none';
        document.getElementById("show-less-" + sectionId).style.display = "block";

        document.getElementById("show-less-" + sectionId).addEventListener('click', function () {
          hiddenRows.style.display = 'none';
          visibleRows.style.display = 'table-row-group';

          this.style.display = 'none';
          document.getElementById("show-more-" + sectionId).style.display = "block";

        });
      });
    });
  });
}

function load(url) {
  const xhr = new XMLHttpRequest();
  xhr.open("GET", url, false);
  xhr.setRequestHeader(
    "Cache-Control",
    "no-cache, no-store, must-revalidate, max-age=0"
  );
  xhr.setRequestHeader("Expires", "0");
  xhr.setRequestHeader("Pragma", "no-cache");
  xhr.send();
  if (xhr.status > 300) {
    return "";
  }
  return xhr.responseText;
}

function ordinal(number) {
  switch (number) {
    case 1:
    case 21:
    case 31:
      return number + 'st';
    case 2:
    case 22:
      return number + 'nd';
    case 3:
    case 23:
      return number + 'rd';
    default:
      return number + 'th';
  }
}

function appendOrdinal() {
  const dates = document.getElementsByClassName('date');
  Array.from(dates).forEach(function (element, index, array) {
    try {
      const date = String(element.innerHTML).trim();
      const parts = date.split(' ');
      const day = ordinal(Number(parts[0]));
      const month = parts[1];
      const year = parts[2];
      element.innerHTML = [day, month, year].join(' ');
    } catch (e) {
      console.warn(e)
    }
  });
}