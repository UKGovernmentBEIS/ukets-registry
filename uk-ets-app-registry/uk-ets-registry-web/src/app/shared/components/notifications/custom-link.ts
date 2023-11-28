import Quill from 'quill';

const Link = Quill.import('formats/link');

/**
 * Custom quill module that prefixes URL with 'http://' in case the user did not add it.
 */
export class CustomLink extends Link {
  static sanitize(url) {
    const value = super.sanitize(url);
    if (value) {
      for (let i = 0; i < this.PROTOCOL_WHITELIST.length; i++)
        if (value.startsWith(this.PROTOCOL_WHITELIST[i])) return value;

      return `http://${value}`;
    }
    return value;
  }
}
