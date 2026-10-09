"""Select VK deployment using the declaration in GameConfig, ignoring comments."""
import os
from pathlib import Path
import re

CONFIG = Path(__file__).resolve().parents[1] / 'core/src/main/java/com/plagame/game/kassa/GameConfig.java'


def target_platform(source):
    source = re.sub(r'("(?:\\.|[^"\\])*")|(/\*[\s\S]*?\*/|//[^\r\n]*)',
                    lambda match: match.group(1) or ' ', source)
    matches = re.findall(r'^\s*public\s+static\s+(?:final\s+)?TargetPlatform\s+TARGET_PLATFORM\s*=\s*TargetPlatform\.([A-Z0-9_]+)\s*;',
                         source, re.MULTILINE)
    if len(matches) != 1:
        raise ValueError('Cannot determine the TARGET_PLATFORM declaration in GameConfig.java')
    return matches[0]


if __name__ == '__main__':
    platform = target_platform(CONFIG.read_text(encoding='utf-8-sig'))
    is_vk = platform == 'HTML_VK'
    print('TARGET_PLATFORM = ' + platform)
    print('VK deployment enabled.' if is_vk else 'Web deployment skipped: this is not a VK build.')
    if os.environ.get('GITHUB_OUTPUT'):
        with open(os.environ['GITHUB_OUTPUT'], 'a', encoding='utf-8') as output:
            output.write('is_vk=' + str(is_vk).lower() + '\n')
