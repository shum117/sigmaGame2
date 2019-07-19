#include <stdlib.h>
#include <nfc/nfc.h>
#include <unistd.h>

static void print_hex(const uint8_t *pbtData, const size_t szBytes)
{
    size_t  szPos;
    
    for (szPos = 0; szPos < szBytes; szPos++) {
        printf("%02X", pbtData[szPos]);
    }
    printf("\n");
}

int main(){
    nfc_device *pnd;
    nfc_target nt;
    nfc_context *context;
    nfc_init(&context);
    if (context == NULL) {
        printf("Unable to init libnfc (malloc)\n");
        exit(EXIT_FAILURE);
    }
    const char *acLibnfcVersion = nfc_version();
    //printf("Uses libnfc %s\n", acLibnfcVersion);
    pnd = nfc_open(context, NULL);
    if (pnd == NULL) {
        printf("ERROR: %s\n", "Unable to open NFC device.");
        exit(EXIT_FAILURE);
    }
    if (nfc_initiator_init(pnd) < 0) {
        nfc_perror(pnd, "nfc_initiator_init");
        exit(EXIT_FAILURE);
    }
    //printf("NFC reader: %s opened\n", nfc_device_get_name(pnd));
    const nfc_modulation nmMifare = {
        .nmt = NMT_ISO14443A,
        .nbr = NBR_106,
    };
    if (nfc_initiator_select_passive_target(pnd, nmMifare, NULL, 0, &nt) > 0) {
        //puts("targetselect");
        if(nfc_initiator_target_is_present(pnd, &nt) == 0){
            print_hex(nt.nti.nai.abtUid, nt.nti.nai.szUidLen);
        }
    }
    nfc_close(pnd);
    nfc_exit(context);
    exit(EXIT_SUCCESS);
}
