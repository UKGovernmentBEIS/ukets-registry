import speakeasy from 'speakeasy';
import jsQR from 'jsqr';
import { delay } from './step.util';
import { assert } from 'chai';
import { KnowsThePage } from './knows-the-page.po';
import {
  addEntryToMap,
  updateByKeyEntryValueAttribute,
} from './user-2fa-map-utils';

export class TwoFactorAuthenticationUtils {
  private qr_code_png_path =
    process.cwd() + '/reports/tests/e2e/execution_outcomes/qr_code.png';

  constructor() {}

  async setSecretByQrCodeImage(
    username: string,
    imageUrl: string,
    width: number,
    height: number
  ) {
    console.log(
      `Entered setSecretByQrCodeImage with arguments:\n imageUrl: ` +
        `'${imageUrl}'.\n username: '${username}'.\n width: '${width}'.\n height: '${height}'.`
    );
    imageUrl = imageUrl.replace(`data:image/png;base64, `, ``);
    let sec = '';
    let qrCode = null;
    const byteNumbers = new Array(imageUrl.length);
    for (let i = 0; i < imageUrl.length; i++) {
      byteNumbers[i] = imageUrl.charCodeAt(i);
    }

    await require('fs').writeFile(
      this.qr_code_png_path,
      imageUrl,
      'base64',
      async (err) => {
        err
          ? console.error(
              `WriteFile: path: '${this.qr_code_png_path}'. Error: '${err}'.`
            )
          : console.log(
              `Image saved successfully in path: '${this.qr_code_png_path}'.`
            );
      }
    );

    let tries = 3;
    let located = false;
    while (!located && tries > 0) {
      if (require('fs').existsSync(this.qr_code_png_path)) {
        located = true;
        console.log(
          `Image saved successfully in path: '${this.qr_code_png_path}'.`
        );
      } else {
        await delay(1000);
        tries--;
      }
    }

    const Jmp = require('jimp');
    let img = null;
    let retry = 3;
    let found = false;
    while (!found && retry > 0) {
      img = await Jmp.read(this.qr_code_png_path);
      if (img !== null) {
        found = true;
      }
      await delay(1000);
      retry--;
    }
    assert.isTrue(found);

    // when image is saved, read it
    try {
      const jmpRead = Jmp.read(this.qr_code_png_path, async (err, image) => {
        if (err) {
          assert.fail(`Error in Jmp.read: '${err}'.`);
        } else {
          console.log(
            `image.bitmap.width: '${image.bitmap.width}'\n` +
              `image.bitmap.height: '${image.bitmap.height}'`
          );

          image.cover(width, height).getBuffer('image/png', async () => {
            qrCode = jsQR(
              new Uint8ClampedArray(image.bitmap.data.buffer),
              image.bitmap.width,
              image.bitmap.height
            );

            // get secret code
            console.log(`Found qrCode in jsQR.`);
            qrCode
              ? (sec = qrCode.data.split('secret=')[1].split('&digits')[0])
              : assert.fail(`No qr code found`);

            // add secret to map if it does not exist / update if it exists due to precious setup of 2fa
            addEntryToMap(KnowsThePage.users2fa, username, {
              secret: sec,
              otp: '',
            });
          });
        }
      });
    } catch (ex) {
      console.error(`Exception in jmpRead: '${ex}'.`);
    }
  }

  async setUserSignIGeneratedSecret(username: string, secretLength: number) {
    try {
      console.log(
        `Entered setUserSignIGeneratedSecret with arguments username '${username}' and secretLength '${secretLength}'.`
      );
      const secret = speakeasy.generateSecret({ length: secretLength });
      const secret_base_32 = secret.base32;
      console.log(`secret_base_32: '${secret_base_32}'.`);
      const secret_ascii = secret.ascii; // equals to convertBase32DataToAscii(secret_base_32)
      console.log(`secret_ascii: '${secret_ascii}'.`);

      addEntryToMap(KnowsThePage.users2fa, username, {
        secret: secret_base_32,
        otp: '',
      });
    } catch (error) {
      console.error('setUserSignIGeneratedSecret error:', error);
    }
  }

  async setVerificationCodeManually(
    username: string,
    secretCode: string,
    algorithmHash: string,
    otpType: string,
    digitsCount: string
  ) {
    console.log(
      `Entered setVerificationCodeManually with argument secretCode '${secretCode}' and username '${username}'.`
    );

    const token = speakeasy.totp({
      secret: secretCode.split(/\s/).join(''),
      encoding: 'base32',
      algorithm: algorithmHash,
      window: 1,
      type: otpType,
      digits: digitsCount,
    });

    updateByKeyEntryValueAttribute(
      KnowsThePage.users2fa,
      username,
      'otp',
      token
    );
  }
}
