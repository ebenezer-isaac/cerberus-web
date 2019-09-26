var textarea = $('.term');
var speed = 50; //Writing speed in milliseconds
var text = 'sh cerberus-web.sh';

var i = 0;

runner();

function runner() {
  textarea.append(text.charAt(i));
  i++;
  setTimeout(
    function() {
      if (i < text.length)
        runner();
      else {
        textarea.append("<br>")
        i = 0;
        setTimeout(function() {feedbacker();}, 1000);
      }
    }, Math.floor(Math.random() * 220) + 50);
}

var count = 0;
var time = 1;
function feedbacker() {
  textarea.append("[    " + count / 1000 + "] " + output[i] + "<br>");
  if (time % 2 == 0) {
    i++;
    textarea.append("[    " + count / 1000 + "] " + output[i] + "<br>");
  }
  if (time == 3) {
    i++;
    textarea.append("[    " + count / 1000 + "] " + output[i] + "<br>");
    i++;
    textarea.append("[    " + count / 1000 + "] " + output[i] + "<br>");
    i++;
    textarea.append("[    " + count / 1000 + "] " + output[i] + "<br>");
  }
  window.scrollTo(0, document.body.scrollHeight);  
  i++;
  time = Math.floor(Math.random() * 4) + 1;
  count += time;
  setTimeout(
    function() {
      if (i < output.length - 2)
        feedbacker();
      else {
        textarea.append("<br>Initialising...<br>");
        setTimeout(function() {$(".load").fadeOut(1000);}, 500);
      }
    },time);
}

var output = ["CPU0 microcode updated early to revision 0x1b, date = 2014-05-29",
"Initializing cgroup subsys cpuset",
"Initializing cgroup subsys cpu",
"Initializing cgroup subsys cpuacct",
"Command line: BOOT_IMAGE=/vmlinuz-3.19.0-21-generic.efi.signed root=UUID=14ac372e-6980-4fe8-b247-fae92d54b0c5",
"KERNEL supported cpus:",
"  Intel GenuineIntel",
"  AMD AuthenticAMD",
"  Centaur CentaurHauls",
"e820: BIOS-provided physical RAM map:",
"BIOS-e820: [mem 0x0000000000000000-0x000000000009dfff] usable",
"BIOS-e820: [mem 0x000000000009e000-0x000000000009ffff] reserved",
"NX (Execute Disable) protection: active",
"efi: EFI v2.31 by American Megatrends",
"efi:  ACPI=0xca852000  ACPI 2.0=0xca852000  SMBIOS=0xca100398 ",
"efi: mem26: [Boot Code          |   |  |  |  |   |WB|WT|WC|UC] range=[0x00000000c973a000-0x00000000c9747000) (0MB)",
"efi: mem56: [Runtime Code       |RUN|  |  |  |   |WB|WT|WC|UC] range=[0x00000000ca03f000-0x00000000ca041000) (0MB)",
"efi: mem57: [Boot Code          |   |  |  |  |   |WB|WT|WC|UC] range=[0x00000000ca041000-0x00000000ca042000) (0MB)",
"SMBIOS 2.7 present.",
"DMI: ASUSTeK COMPUTER INC. N56VZ/N56VZ, BIOS N56VZ.217 05/22/2013",
"e820: update [mem 0x00000000-0x00000fff] usable ==> reserved",
"CPU1 microcode updated early to revision 0x1b, date = 2014-05-29",
"NMI watchdog: enabled on all CPUs, permanently consumes one hw-PMU counter.",
" #2",
"CPU2 microcode updated early to revision 0x1b, date = 2014-05-29",
"Initialising...", ""];