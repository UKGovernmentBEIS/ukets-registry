import Quill from 'quill';

const Block = Quill.import('blots/block');

/**
 * Extends the block to replace the divider between lines from
 * <p> to <div>.
 */
export class CustomParchmentBlock extends Block {}

// this is a weird way to customize the Block but it seems to be the way to go
CustomParchmentBlock.tagName = 'DIV';
